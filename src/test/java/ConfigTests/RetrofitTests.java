package ConfigTests;

import datalogic.DataLogicApplication;
import datalogic.config.RetrofitConfig;
import datalogic.config.RetrofitProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataLogicApplication.class})
@EnableConfigurationProperties(RetrofitProperties.class)
@SpringBootTest
public class RetrofitTests {

    @Autowired
    RetrofitConfig retrofitConfig;

    @Autowired
    RetrofitProperties retrofitProperties;

    @Test
    public void retrofitConfigTest(){
        assertNotNull(retrofitConfig);
    }

    @Test
    public void retrofitPropertyTest(){
        assertNotNull(retrofitProperties);
        assertNotNull(retrofitProperties.getEndpoints().get(0));
    }

}
