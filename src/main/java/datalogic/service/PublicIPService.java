package datalogic.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Service
class PublicIPService { //locates user's current public ip address by making a request to the given url below.
    private static String PUBLIC_IP_ADDRESS;

    protected String getPUBLIC_IP_ADDRESS() {
        return PUBLIC_IP_ADDRESS;
    }

    @PostConstruct
    private void setPUBLIC_IP_ADDRESS() throws IOException {
        String REQUEST_FOR_PUBLIC_IP_ADDRESS = "http://checkip.amazonaws.com/";
        URL url = new URL(REQUEST_FOR_PUBLIC_IP_ADDRESS);
        try(BufferedReader buffer = new BufferedReader(new InputStreamReader(url.openStream()))) {
            PUBLIC_IP_ADDRESS = buffer.readLine();
        }
    }
}
