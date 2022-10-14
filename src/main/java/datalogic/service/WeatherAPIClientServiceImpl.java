package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class WeatherAPIClientServiceImpl{

    private final WeatherAPIClientService weatherAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;

    @Autowired
    public WeatherAPIClientServiceImpl(WeatherAPIClientService weatherRestAPI, List<EndpointProperty> restEndpoints, ServiceUtil serviceUtil) {
        this.weatherAPIClientService = weatherRestAPI;
        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
    }

    public Weather getCurrentWeatherData(Double latitude, Double longitude) throws ExecutionException, InterruptedException {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_currentWeather_API");
        return this.weatherAPIClientService.getWeatherData(endpoint.getBaseUrl(), latitude, longitude, endpoint.getAPI_key()).get();
    }

    public Weather getHourlyWeatherData(Double latitude, Double longitude) throws ExecutionException, InterruptedException {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_hourlyWeather_API");
        return this.weatherAPIClientService.getWeatherData(endpoint.getBaseUrl(), latitude, longitude, endpoint.getAPI_key()).get();
    }

    public Weather getDailyWeatherData(Double latitude, Double longitude) throws ExecutionException, InterruptedException {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_dailyWeather_API");
        return this.weatherAPIClientService.getWeatherData(endpoint.getBaseUrl(), latitude, longitude, endpoint.getAPI_key()).get();
    }
}
