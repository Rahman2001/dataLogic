package datalogic.service.serviceImpl;

import datalogic.config.EndpointProperty;
import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@Service
public class ApiServiceUtil {
    private final WeatherAPIClientServiceImpl weatherService;
    private final HourlyWeatherAPIClientServiceImpl hourlyWeatherService;
    private final DailyWeatherAPIClientServiceImpl dailyWeatherService;
    @Autowired
    public ApiServiceUtil(WeatherAPIClientServiceImpl weatherAPIClientService,
                          HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientService,
                          DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService) {
        this.weatherService = weatherAPIClientService;
        this.hourlyWeatherService = hourlyWeatherAPIClientService;
        this.dailyWeatherService = dailyWeatherAPIClientService;
    }

    public Map<String, EndpointProperty> groupsEndpoints(List<EndpointProperty> endpointProperties) {
        return endpointProperties.stream().collect(toImmutableMap(EndpointProperty::getServiceName, Function.identity()));
    }
    public Map<String, ? extends Weather> callAll(@NotNull String city) {
        List<CompletableFuture<? extends Weather>> weathers = this.getAllWeathers(city);
        return CompletableFuture.allOf(weathers.toArray(CompletableFuture[]::new))
                .thenApply(f-> weathers.stream().map(CompletableFuture::join)
                        .collect(Collectors.toMap(w-> w.getClass().getName(), Function.identity())))
                .join();
    }
    public Map<String, ? extends Weather> callAll(@NotNull Double lat, @NotNull Double lon) {
        List<CompletableFuture<? extends Weather>> weathers = this.getAllWeathers(lat, lon);
        return CompletableFuture.allOf(weathers.toArray(CompletableFuture[]::new))
                .thenApply(f-> weathers.stream().map(CompletableFuture::join)
                        .collect(Collectors.toMap(w-> w.getClass().getName(), Function.identity())))
                .join();
    }
    private List<CompletableFuture<? extends Weather>> getAllWeathers(String city) {
        CompletableFuture<Weather> currentWeather = CompletableFuture.supplyAsync(()-> this.weatherService.getCurrentWeatherData(city));
        CompletableFuture<HourlyWeather> hourlyWeather = CompletableFuture.supplyAsync(()-> this.hourlyWeatherService.getHourlyWeather(city));
        CompletableFuture<DailyWeather> dailyWeather = CompletableFuture.supplyAsync(()-> this.dailyWeatherService.getDailyWeather(city));
        return List.of(currentWeather, hourlyWeather, dailyWeather);
    }
    private List<CompletableFuture<? extends Weather>> getAllWeathers(Double lat, Double lon) {
        CompletableFuture<Weather> currentWeather = CompletableFuture.supplyAsync(()-> this.weatherService.getCurrentWeatherData(lat, lon));
        CompletableFuture<HourlyWeather> hourlyWeather = CompletableFuture.supplyAsync(()-> this.hourlyWeatherService.getHourlyWeather(lat, lon));
        CompletableFuture<DailyWeather> dailyWeather = CompletableFuture.supplyAsync(()-> this.dailyWeatherService.getDailyWeather(lat,lon));
        return List.of(currentWeather, hourlyWeather, dailyWeather);
    }
}
