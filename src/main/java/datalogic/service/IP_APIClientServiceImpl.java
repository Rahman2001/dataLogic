package datalogic.service;

import datalogic.config.EndpointProperty;
import datalogic.model.UserLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

@Service
@Slf4j
public class IP_APIClientServiceImpl {
    private final IP_APIClientService ip_api_service;
    private final EndpointProperty endpointProperty;
    //private final ServiceUtil serviceUtil;

    @Autowired
    public IP_APIClientServiceImpl(@Qualifier("restEndpoints") final List<EndpointProperty> restEndpoints,
                                   final IP_APIClientService ip_api_service,
                                   ServiceUtil serviceUtil){
        this.ip_api_service = ip_api_service;
        this.endpointProperty = serviceUtil.groupsEndpoints(restEndpoints).get("IP_API");
       // this.serviceUtil = serviceUtil;
    }

    public UserLocation getUserLocation() {
        try {
            return this.ip_api_service.getUserLocation(endpointProperty.getApiKey()).get();
        }
        catch (Exception e){
            log.error("Cannot return user location! - ", e);
            return null;
        }
    }

    @PostConstruct
    private String getUserIp(){
        String ip;
        try {
            URL url = new URL("http://checkip.amazonaws.com/");
            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));
            ip = bf.readLine().trim();
        }
        catch (Exception e){
            ip = null;
        }
        return ip;
    }
}
