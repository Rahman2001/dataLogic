package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.GeolocationByIPAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class IPGeolocationAPIClientServiceImpl {
    private final IPGeolocationAPIClientService ipGeolocationAPIClientService;
    private final Map<String, EndpointProperty> endpointPropertyMap;
    private final PublicIPService publicIPService;

    @Autowired
    public IPGeolocationAPIClientServiceImpl(List<EndpointProperty> restEndpoints,
                                             IPGeolocationAPIClientService ipGeolocationAPIClientService,
                                             ServiceUtil serviceUtil, PublicIPService publicIPService) {

        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
        this.ipGeolocationAPIClientService = ipGeolocationAPIClientService;
        this.publicIPService = publicIPService;
    }

    public CompletableFuture<GeolocationByIPAddress> getIPGeolocation() {
        EndpointProperty endpointProperty = this.endpointPropertyMap.get("IPGeolocation_API");

        return this.ipGeolocationAPIClientService.locateIPAddress(endpointProperty.getBaseUrl(),
                this.publicIPService.getPUBLIC_IP_ADDRESS(),
                endpointProperty.getAPI_key());
    }
}
