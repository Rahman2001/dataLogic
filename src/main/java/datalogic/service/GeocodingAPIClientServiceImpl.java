package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.GeocodingByCityName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GeocodingAPIClientServiceImpl {
    private final GeocodingAPIClientService geocodingAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;
    private final ServiceUtil serviceUtil;

    @Autowired
    public GeocodingAPIClientServiceImpl(@Qualifier("restEndpoints") List<EndpointProperty> restEndpoints,
                                         @Qualifier("geocodingAPIClientService") GeocodingAPIClientService geocodingAPIClientService,
                                         ServiceUtil serviceUtil) {

        this.geocodingAPIClientService = geocodingAPIClientService;
        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
        this.serviceUtil = serviceUtil;
    }

    public CompletableFuture<GeocodingByCityName> getCoordinatesByCity(String cityName) {
        EndpointProperty restAPI = this.endpointPropertyMap.get("Geocoding_API");
        return this.geocodingAPIClientService.convertGeolocationToCoordinates(this.serviceUtil.urlBuilder(restAPI.getBaseUrl(), restAPI.getPath(), cityName, restAPI.getApiKey()));
    }
}
