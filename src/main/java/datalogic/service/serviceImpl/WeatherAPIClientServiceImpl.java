package datalogic.service.serviceImpl;

import datalogic.config.EndpointProperty;
import datalogic.model.Weather;
import datalogic.service.clientService.WeatherAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WeatherAPIClientServiceImpl{

    private final WeatherAPIClientService weatherAPIClientService;
    private final EndpointProperty endpoint;

    @Autowired
    public WeatherAPIClientServiceImpl(final WeatherAPIClientService weatherRestAPI,
                                       final @Qualifier("restEndpoints") List<EndpointProperty> restEndpoints,
                                       final ServiceUtil serviceUtil) {
        this.weatherAPIClientService = weatherRestAPI;
        this.endpoint = serviceUtil.groupsEndpoints(restEndpoints).get("OpenWeatherMap_currentWeather_API");
    }

    public Weather getCurrentWeatherData(Double latitude, Double longitude) {
        try {
            return this.weatherAPIClientService.getWeatherData(endpoint.getPath(), latitude, longitude,
                    endpoint.getApiKey(), endpoint.getWeatherUnit()).get();
        } catch (Exception e) {
            log.error("Could not return current weather data! - ", e);
            return null;
        }
    }
    public Weather getCurrentWeatherData(String city) {
        try {
            return this.weatherAPIClientService.getWeatherData(this.endpoint.getPath(), city,
                    this.endpoint.getApiKey(), this.endpoint.getWeatherUnit()).get();
        }
        catch (Exception e) {
            log.error("Could not return current weather data! - ", e);
            return null;
        }
    }
}
