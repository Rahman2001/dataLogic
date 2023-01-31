package datalogic.service.serviceImpl;

import datalogic.config.EndpointProperty;
import datalogic.model.HourlyWeather;
import datalogic.service.clientService.HourlyWeatherAPIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HourlyWeatherAPIClientServiceImpl {
    private final HourlyWeatherAPIClientService hourlyWeatherAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;

    @Autowired
    public HourlyWeatherAPIClientServiceImpl(final @Qualifier("restEndpoints")List<EndpointProperty> restEndpoints,
                                             final HourlyWeatherAPIClientService hourlyWeatherAPIClientService,
                                             final ServiceUtil serviceUtil){
        this.hourlyWeatherAPIClientService = hourlyWeatherAPIClientService;
        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
    }

    public HourlyWeather getHourlyWeather(Double lat, Double lon) {
        EndpointProperty endpointProperty = this.endpointPropertyMap.get("OpenWeatherMap_hourlyWeather_API");
        try{
            return this.hourlyWeatherAPIClientService.getHourlyWeather(endpointProperty.getPath(), lat, lon,
                    endpointProperty.getApiKey(), endpointProperty.getWeatherUnit()).get();
        }
        catch (Exception e){
            log.error("Could not return hourly weather forecast! - ", e);
            return null;
        }
    }
}
