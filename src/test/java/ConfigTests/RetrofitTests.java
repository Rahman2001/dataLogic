package ConfigTests;

import datalogic.config.RetrofitConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RetrofitConfig.class})
public class RetrofitConfigTest {

    @Autowired
    RetrofitConfig retrofitConfig;

    @Test
    public void retrofitConfigTest(){
        assertNotNull(retrofitConfig);
        assertNotNull(retrofitConfig.getEndpointProperties());
        System.out.println(retrofitConfig.getEndpointProperties().toString());
    }
}
