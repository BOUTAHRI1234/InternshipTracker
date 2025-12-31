package eirb.mobile.internshiptracker.service;

import android.util.Log;
import eirb.mobile.internshiptracker.network.GroqService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiAnalyzer {
    private GroqService groqService;

    public AiAnalyzer(String apiKey) {

        // Log.d("MistralDebug", "Clé reçue : '" + apiKey + "'");
        String finalkey = apiKey.trim();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + finalkey)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.groq.com/openai/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        groqService = retrofit.create(GroqService.class);
    }

    public JsonObject analyzeEmail(String subject, String body, String sender, String date) {
        String prompt = "You are a backend JSON parser. Analyze this email:\n" +
                "Subject: " + subject + "\n" +
                "From: " + sender + "\n" +
                "Date: " + date + "\n" +
                "Body:\n" + body + "\n\n" +
                "Task: Extract company name, position, status (Awaiting Reply, Rejected, Accepted), and a short summary.\n" +
                "IMPORTANT: Output ONLY valid JSON. No Markdown. No Intro. No Outro.\n" +
                "Format: { \"company\": \"...\", \"position\": \"...\", \"status\": \"...\", \"summary\": \"...\" }";

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "meta-llama/llama-4-scout-17b-16e-instruct");
        requestBody.addProperty("temperature", 0.1);

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        messages.add(message);

        requestBody.add("messages", messages);
        JsonObject responseFormat = new JsonObject();
        responseFormat.addProperty("type", "json_object");
        requestBody.add("response_format", responseFormat);

        try {
            Response<JsonObject> response = groqService.getChatCompletion(requestBody).execute();
            if (response.isSuccessful() && response.body() != null) {
                String content = response.body()
                        .getAsJsonArray("choices").get(0).getAsJsonObject()
                        .getAsJsonObject("message").get("content").getAsString();

                content = content.replace("```json", "").replace("```", "").trim();
                return JsonParser.parseString(content).getAsJsonObject();
            } else {
                Log.e("Groq", "Error: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}