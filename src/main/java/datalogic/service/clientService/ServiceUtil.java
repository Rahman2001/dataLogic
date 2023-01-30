package datalogic.service.clientService;

import datalogic.config.EndpointProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@Service
public class ServiceUtil {
    public Map<String, EndpointProperty> groupsEndpoints(List<EndpointProperty> endpointProperties) {
        return endpointProperties.stream().collect(toImmutableMap(EndpointProperty::getServiceName, Function.identity()));
    }
    protected String urlBuilder(String baseUrl, String path, String cityName, String apiKey){
        return baseUrl + path + "?q=" + cityName + "&appid=" + apiKey;
    }
    protected String urlBuilder(String baseUrl, String path){
        return baseUrl + path;
    }
    protected String urlBuilder(String baseUrl, String path, Double lat, Double lon, String apiKey){
        return baseUrl + path + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
    }
}
