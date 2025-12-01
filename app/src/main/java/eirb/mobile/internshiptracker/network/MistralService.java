package eirb.mobile.internshiptracker.network;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MistralService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    Call<JsonObject> getChatCompletion(@Body JsonObject body);
}