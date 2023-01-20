package datalogic.service;

import datalogic.model.UserLocation;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

public interface IP_APIClientService {
    @GET("{apiKey}")
    CompletableFuture<UserLocation> getUserLocation(@Path("apiKey") String apiKey);
}
