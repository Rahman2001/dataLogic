package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@Component
public class WeatherAPIClientServiceImpl {

    private final WeatherAPIClientService weatherAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;

    @Autowired
    public WeatherAPIClientServiceImpl(WeatherAPIClientService weatherRestAPI, List<EndpointProperty> restEndpoints) {
        this.weatherAPIClientService = weatherRestAPI;
        this.endpointPropertyMap = groupsEndpoints(restEndpoints);
    }

    private Map<String, EndpointProperty> groupsEndpoints(List<EndpointProperty> endpointProperties) {
        return endpointProperties.stream().collect(toImmutableMap(EndpointProperty::getServiceName, Function.identity()));
    }

    public WeatherData getCurrentWeatherData(Double latitude, Double longitude) throws ExecutionException, InterruptedException {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_currentWeather_API");
        return this.weatherAPIClientService.getWeatherData(endpoint.getBaseUrl(), latitude, longitude, endpoint.getAPI_key()).get();
    }

    public WeatherData getHourlyWeatherData(Double latitude, Double longitude) throws ExecutionException, InterruptedException {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_hourlyWeather_API");
        return this.weatherAPIClientService.getWeatherData(endpoint.getBaseUrl(), latitude, longitude, endpoint.getAPI_key()).get();
    }

    public WeatherData getDailyWeatherData(Double latitude, Double longitude) throws ExecutionException, InterruptedException {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_dailyWeather_API");
        return this.weatherAPIClientService.getWeatherData(endpoint.getBaseUrl(), latitude, longitude, endpoint.getAPI_key()).get();
    }
}
