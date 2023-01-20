package datalogic.service;

import datalogic.model.DailyWeather;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.util.concurrent.CompletableFuture;

public interface DailyWeatherAPIClientService {
    @GET
    CompletableFuture<DailyWeather> getDailyWeather(@Url String fullUrl);
}
