package serviceImplTests.clientServiceTest;

import datalogic.config.RestServiceForRetrofit;
import datalogic.config.RetrofitConfig;
import datalogic.config.RetrofitProperties;
import datalogic.service.clientService.ServiceUtil;
import datalogic.service.clientService.WeatherAPIClientService;
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
public class WeatherAPIClientServiceTest {
    @Autowired
    WeatherAPIClientService weatherAPIClientService;

    @Test
    public void createClient(){ // since it depends on too many parameters, we only test successful creation of a client
        assertNotNull(this.weatherAPIClientService);
    }
}
