package datalogic.service;

import datalogic.model.DailyWeather;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

public interface DailyWeatherAPIClientService {
    @GET("{path}")
    CompletableFuture<DailyWeather> getDailyWeather(@Path("path") String path, @Query("lat") Double lat,
                                                    @Query("lon") Double lon, @Query("appid") String apiKey,
                                                    @Query("units") String weatherUnit);
}
