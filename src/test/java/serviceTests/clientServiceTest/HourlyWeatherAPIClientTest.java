package serviceTests.clientServiceTest;

import datalogic.config.RestServiceForRetrofit;
import datalogic.config.RetrofitConfig;
import datalogic.config.RetrofitProperties;
import datalogic.service.clientService.HourlyWeatherAPIClientService;
import datalogic.service.serviceImpl.ServiceUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceUtil.class, RestServiceForRetrofit.class, RetrofitConfig.class, RetrofitProperties.class})
@SpringBootTest
public class HourlyWeatherAPIClientTest {
    @Autowired
    HourlyWeatherAPIClientService hourlyWeatherAPIClientService;

    @Test
    public void createClient(){ // since it depends on too many parameters, we only test successful creation of a client
        assertNotNull(this.hourlyWeatherAPIClientService);
    }
}
