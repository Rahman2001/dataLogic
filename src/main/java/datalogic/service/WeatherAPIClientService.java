package datalogic.service;

import datalogic.model.Weather;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.concurrent.CompletableFuture;

public interface WeatherAPIClientService { //this client service requests weather information for particular location.
    @GET("{baseURL}" + "{lat}" + "{lon}" + "{appid}" + "&units=metric")
    CompletableFuture<Weather> getWeatherData(@Url @Path(value = "baseURL") String baseURL,
                                              @Query(value = "lat") Double latitude,
                                              @Query(value = "lon") Double longitude,
                                              @Query(value = "appid") String API_key);

}