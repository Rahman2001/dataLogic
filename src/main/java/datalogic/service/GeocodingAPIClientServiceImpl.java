package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.GeocodingByCityName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GeocodingAPIClientServiceImpl {
    private final GeocodingAPIClientService geocodingAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;

    @Autowired
    public GeocodingAPIClientServiceImpl(List<EndpointProperty> restEndpoints,
                                         GeocodingAPIClientService geocodingAPIClientService,
                                         ServiceUtil serviceUtil) {

        this.geocodingAPIClientService = geocodingAPIClientService;
        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
    }

    public CompletableFuture<GeocodingByCityName> getCoordinatesByCity(String cityName) {
        EndpointProperty restAPI = this.endpointPropertyMap.get("Geocoding_API");
        return this.geocodingAPIClientService.convertGeolocationToCoordinates(restAPI.getBaseUrl(), cityName, restAPI.getAPI_key());
    }
}
