package datalogic.service;

import datalogic.model.UserLocation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.concurrent.CompletableFuture;

public interface IP_APIClientService {
    @GET("{ipAddress}")
    CompletableFuture<UserLocation> getUserLocation(@Path("ipAddress") String ipAddress);
}
