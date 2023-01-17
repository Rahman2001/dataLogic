package datalogic.service;

import datalogic.model.GeocodingByCityName;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.concurrent.CompletableFuture;

public interface GeocodingAPIClientService { //this client requests coordinates (latitude, longitude) of particular city

    @GET
    CompletableFuture<GeocodingByCityName> convertGeolocationToCoordinates(@Url String Geocoding_API);
}
