package datalogic.service.clientService;

import datalogic.model.HourlyWeather;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.concurrent.CompletableFuture;

public interface HourlyWeatherAPIClientService {
    @GET("{path}")
    CompletableFuture<HourlyWeather> getHourlyWeather(@Path("path") String path, @Query("lat") Double lat,
                                                      @Query("lon") Double lon, @Query("appid") String apiKey,
                                                      @Query("cnt") Integer numberOfHours, @Query("units") String weatherUnit);

    @GET("{path}")
    CompletableFuture<HourlyWeather> getHourlyWeather(@Path("path") String path, @Query("q") String city,
                                                      @Query("appid") String apiKey, @Query("cnt") Integer numberOfHours,
                                                      @Query("units") String weatherUnit);
}
