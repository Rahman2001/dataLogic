package datalogic.service;

import datalogic.model.Weather;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

public interface WeatherAPIClientService { //this client service requests weather information for particular location.
    @GET("{path}")
    CompletableFuture<Weather> getWeatherData(@Path("path") String path,
                                              @Query(value = "lat") Double latitude,
                                              @Query(value = "lon") Double longitude,
                                              @Query(value = "appid") String API_key,
                                              @Query(value = "units") String weatherUnit);

}