package datalogic.service.serviceImpl;

import datalogic.model.UserLocation;
import datalogic.service.clientService.IP_APIClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Service
@Slf4j
public class IP_APIClientServiceImpl {
    private final IP_APIClientService ip_api_service;
    private String ipAddress;

    @Autowired
    public IP_APIClientServiceImpl(final IP_APIClientService ip_api_service){
        this.ip_api_service = ip_api_service;
    }

    public UserLocation getUserLocation() {
        try {
            return this.ip_api_service.getUserLocation(this.ipAddress).join();
        }
        catch (Exception e){
            log.error("Cannot return user location! - ", e);
            return null;
        }
    }

    @PostConstruct
    private void getUserIp(){
        try {
            URL url = new URL("http://checkip.amazonaws.com/");
            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));
            ipAddress = bf.readLine().trim();
        }
        catch (Exception e){
            ipAddress = null;
        }
    }
}
