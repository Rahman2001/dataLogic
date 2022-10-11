package datalogic.service;

import datalogic.model.GeocodingByCityName;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.concurrent.CompletableFuture;

public interface GeocodingAPIClientService { //this client requests coordinates (latitude, longitude) of particular city
    @GET("{baseUrl}" + "{q}" + "{API_key}")
    CompletableFuture<GeocodingByCityName> convertGeolocationToCoordinates(@Url @Path(value = "baseUrl") String OpenWeatherMap_Geocoding_baseUrl,
                                                                           @Query("q") String city_name, @Query("appid") String API_key);
}
