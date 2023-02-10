package datalogic.service.serviceImpl;

import datalogic.config.EndpointProperty;
import datalogic.model.DailyWeather;
import datalogic.service.clientService.DailyWeatherAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DailyWeatherAPIClientServiceImpl {
    private final DailyWeatherAPIClientService dailyWeatherAPIClientService;
    private final EndpointProperty endpoint;

    @Autowired
    public DailyWeatherAPIClientServiceImpl(final DailyWeatherAPIClientService dailyWeatherAPIClientService,
                                            final @Qualifier("restEndpoints") List<EndpointProperty> restEndpoints,
                                            ApiServiceUtil apiServiceUtil){
        this.dailyWeatherAPIClientService = dailyWeatherAPIClientService;
        this.endpoint = apiServiceUtil.groupsEndpoints(restEndpoints).get("OpenWeatherMap_dailyWeather_API");
    }

    public DailyWeather getDailyWeather(Double lat, Double lon){
        try {
            return this.dailyWeatherAPIClientService.getDailyWeather(endpoint.getPath(), lat, lon,
                    endpoint.getApiKey(), endpoint.getWeatherUnit()).join();
        }
        catch (Exception e){
            log.error("Could not return daly weather forecast! - ", e);
            return null;
        }
    }
    public DailyWeather getDailyWeather(String city) {
        try {
            return this.dailyWeatherAPIClientService.getDailyWeather(endpoint.getPath(), city, endpoint.getApiKey(),
                    endpoint.getWeatherUnit()).join();
        }
        catch (Exception e) {
            log.error("Could not return daly weather forecast! - ", e);
            return null;
        }
    }
}
