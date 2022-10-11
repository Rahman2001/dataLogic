package datalogic.service;

import datalogic.model.GeolocationByIP;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.concurrent.CompletableFuture;

public interface IPGeolocationAPIClientService { //this client service requests location of user's IP address
    @GET("{baseURL}" + "{ipAddress}" + "{q}")
    CompletableFuture<GeolocationByIP> locateIPAddress(@Url @Path(value = "baseURL") String baseURL,
                                                       @Path(value = "ipAddress") String ipAddress,
                                                       @Query(value = "q") String API_key);
}
