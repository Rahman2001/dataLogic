package datalogic.service.serviceImpl;

import datalogic.config.EndpointProperty;
import datalogic.model.HourlyWeather;
import datalogic.service.ServiceUtil;
import datalogic.service.clientService.HourlyWeatherAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HourlyWeatherAPIClientServiceImpl {
    private final HourlyWeatherAPIClientService hourlyWeatherAPIClientService;
    private final EndpointProperty endpoint;

    @Autowired
    public HourlyWeatherAPIClientServiceImpl(final @Qualifier("restEndpoints")List<EndpointProperty> restEndpoints,
                                             final HourlyWeatherAPIClientService hourlyWeatherAPIClientService,
                                             final ServiceUtil serviceUtil){
        this.hourlyWeatherAPIClientService = hourlyWeatherAPIClientService;
        this.endpoint = serviceUtil.groupsEndpoints(restEndpoints).get("OpenWeatherMap_hourlyWeather_API");
    }

    public HourlyWeather getHourlyWeather(Double lat, Double lon) {
        try{
            return this.hourlyWeatherAPIClientService.getHourlyWeather(endpoint.getPath(), lat, lon,
                    endpoint.getApiKey(), 5, endpoint.getWeatherUnit()).get();
        }
        catch (Exception e){
            log.error("Could not return hourly weather forecast! - ", e);
            return null;
        }
    }

    public HourlyWeather getHourlyWeather(String city) {
        try {
            return this.hourlyWeatherAPIClientService.getHourlyWeather(endpoint.getPath(), city, endpoint.getApiKey(),
                    5, endpoint.getWeatherUnit()).join();
        }
        catch (Exception e) {
            log.error("Could not return hourly weather forecast! - ", e);
            return null;
        }
    }
}
