package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.DailyWeather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DailyWeatherAPIClientServiceImpl {
    private final DailyWeatherAPIClientService dailyWeatherAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;
    private final ServiceUtil serviceUtil;

    @Autowired
    public DailyWeatherAPIClientServiceImpl(final DailyWeatherAPIClientService dailyWeatherAPIClientService,
                                            final @Qualifier("restEndpoints") List<EndpointProperty> restEndpoints,
                                            ServiceUtil serviceUtil){
        this.dailyWeatherAPIClientService = dailyWeatherAPIClientService;
        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
        this.serviceUtil = serviceUtil;
    }

    public DailyWeather getDailyWeather(Double lat, Double lon){
        EndpointProperty endpointProperty = this.endpointPropertyMap.get("OpenWeatherMap_dailyWeather_API");
        try {
            return this.dailyWeatherAPIClientService.getDailyWeather(endpointProperty.getPath(), lat, lon,
                    endpointProperty.getApiKey(), endpointProperty.getWeatherUnit()).get();
        }
        catch (Exception e){
            log.error("Could not return daly weather forecast! - ", e);
            return null;
        }
    }
}
