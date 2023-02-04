package serviceTests.serviceImplTests;

import datalogic.config.EndpointProperty;
import datalogic.model.DailyWeather;
import datalogic.service.clientService.DailyWeatherAPIClientService;
import datalogic.service.serviceImpl.DailyWeatherAPIClientServiceImpl;
import datalogic.service.serviceImpl.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/* We used Answers.RETURNS_DEEP_STUBS for ServiceUtil.class because we want to mock EndpointProperty in method-under-test (.getDailyWeatherForCity(Double lat, Double lon))
ServiceUtil.groupByEndpoints() passes mocked data to Map that is used by EndpointProperty at the end. Thus, we insert mocked argument into argument of method-under-test.
 */
// For more details, documentation: https://www.javadoc.io/doc/org.mockito/mockito-core/2.2.9/org/mockito/Mockito.html#RETURNS_DEEP_STUBS

@ExtendWith({MockitoExtension.class})
@ContextConfiguration(classes = {ServiceUtil.class})
public class DailyWeatherAPIServiceImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DailyWeatherAPIClientService dailyWeatherAPIClientService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServiceUtil serviceUtil;
    private Map<String, EndpointProperty> endpointPropertyMap;

    @InjectMocks
    DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientServiceImpl;

    @BeforeEach
    public void creatStubbing(){
        EndpointProperty endpointProperty = EndpointProperty.builder().apiKey("Rahman").weatherUnit("metric")
                .path("adsadsa").build();
        this.endpointPropertyMap = new HashMap<>();
        this.endpointPropertyMap.put("rahman", endpointProperty);
    }

    @Test
    public void getDailyWeatherTest(){
        when(this.serviceUtil.groupsEndpoints(anyList())).thenReturn(this.endpointPropertyMap);
        when(this.dailyWeatherAPIClientService.getDailyWeather(anyString(), anyDouble(), anyDouble(), anyString(), anyString()).join())
                .thenReturn(new DailyWeather());
        assertNotNull(this.dailyWeatherAPIClientServiceImpl.getDailyWeather(0.0, 0.0));
    }
}
