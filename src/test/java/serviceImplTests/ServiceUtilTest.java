package serviceImplTests;

import datalogic.config.EndpointProperty;
import datalogic.config.RetrofitConfig;
import datalogic.config.RetrofitProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RetrofitProperties.class, RetrofitConfig.class})
public class ServiceUtilTest {
    @Autowired
    RetrofitProperties retrofitProperties;
    @Test
    public void serviceUtilTest(){
        datalogic.service.ServiceUtil serviceUtil = new datalogic.service.ServiceUtil();
        List<EndpointProperty> endpointProperties = retrofitProperties.getEndpoints();
        Map<String, EndpointProperty> endpointPropertyMap = serviceUtil.groupsEndpoints(endpointProperties);
        assertTrue("No element in the Map", endpointPropertyMap.size() != 0);
    }
}
