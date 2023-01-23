package serviceTests;

import datalogic.config.RestServiceForRetrofit;
import datalogic.config.RetrofitConfig;
import datalogic.config.RetrofitProperties;
import datalogic.service.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceUtil.class, RestServiceForRetrofit.class, RetrofitProperties.class, RetrofitConfig.class})
@SpringBootTest
public class IP_APIClientService {

    @Autowired
    datalogic.service.IP_APIClientService ip_apiClientService;
    private final String ipAddressExample = "24.48.0.1";

    @BeforeEach
    public void createClient(){
        assertNotNull(this.ip_apiClientService);
    }

    @Test
    public void ipClientTest(){
        assertNotNull(this.ip_apiClientService.getUserLocation(this.ipAddressExample));
        assertFalse(this.ip_apiClientService.getUserLocation(this.ipAddressExample).join().getCity().isEmpty());
    }
}
