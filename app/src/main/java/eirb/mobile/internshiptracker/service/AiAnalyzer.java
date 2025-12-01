package eirb.mobile.internshiptracker.service;

import android.util.Log;
import eirb.mobile.internshiptracker.network.MistralService;
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
    private MistralService mistralService;

    public AiAnalyzer(String apiKey) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + apiKey)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.mistral.ai/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mistralService = retrofit.create(MistralService.class);
    }

    public JsonObject analyzeEmail(String subject, String body, String sender, String date) {
        String prompt = "You are a structured data extraction assistant.\n" +
                "Analyze this email:\n" +
                "Subject: " + subject + "\n" +
                "From: " + sender + "\n" +
                "Date: " + date + "\n" +
                "Body:\n" + body + "\n\n" +
                "Extract: company name, position, status (Awaiting Reply, Rejected, Accepted), and a summary.\n" +
                "Return ONLY a JSON object with keys: company, position, status, summary.";

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "mistral-small-latest");
        requestBody.addProperty("temperature", 0.2);

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        messages.add(message);

        requestBody.add("messages", messages);
        requestBody.addProperty("response_format", "{ \"type\": \"json_object\" }");

        try {
            Response<JsonObject> response = mistralService.getChatCompletion(requestBody).execute();
            if (response.isSuccessful() && response.body() != null) {
                String content = response.body()
                        .getAsJsonArray("choices").get(0).getAsJsonObject()
                        .getAsJsonObject("message").get("content").getAsString();

                content = content.replace("```json", "").replace("```", "").trim();
                return JsonParser.parseString(content).getAsJsonObject();
            } else {
                Log.e("Mistral", "Error: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}