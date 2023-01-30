package datalogic.service.clientService;

import datalogic.config.EndpointProperty;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WeatherAPIClientServiceImpl{

    private final WeatherAPIClientService weatherAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;

    @Autowired
    public WeatherAPIClientServiceImpl(final WeatherAPIClientService weatherRestAPI,
                                       final @Qualifier("restEndpoints") List<EndpointProperty> restEndpoints,
                                       final ServiceUtil serviceUtil) {
        this.weatherAPIClientService = weatherRestAPI;
        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
    }

    public Weather getCurrentWeatherData(Double latitude, Double longitude) {
        EndpointProperty endpoint = this.endpointPropertyMap.get("OpenWeatherMap_currentWeather_API");
        try {
            return this.weatherAPIClientService.getWeatherData(endpoint.getPath(), latitude, longitude,
                    endpoint.getApiKey(), endpoint.getWeatherUnit()).get();
        } catch (Exception e) {
            log.error("Could not return current weather data! - ", e);
            return null;
        }
    }
}
