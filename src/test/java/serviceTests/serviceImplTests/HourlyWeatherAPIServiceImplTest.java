package serviceTests.serviceImplTests;

import datalogic.config.EndpointProperty;
import datalogic.config.RetrofitConfig;
import datalogic.config.RetrofitProperties;
import datalogic.model.HourlyWeather;
import datalogic.service.clientService.HourlyWeatherAPIClientService;
import datalogic.service.serviceImpl.HourlyWeatherAPIClientServiceImpl;
import datalogic.service.serviceImpl.ServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ServiceUtil.class})
@SpringBootTest(classes = {RetrofitConfig.class, RetrofitProperties.class})
public class HourlyWeatherAPIServiceImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServiceUtil serviceUtil;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HourlyWeatherAPIClientService client;
    @MockBean(name = "restEndpoints")
    private List<EndpointProperty> restEndpoints;
    @InjectMocks
    HourlyWeatherAPIClientServiceImpl serviceImpl;
    private Map<String, EndpointProperty> endpointPropertyMap;

    @BeforeEach
    public void createClient() {
        this.endpointPropertyMap = new HashMap<>();
        this.endpointPropertyMap.put("rahman", EndpointProperty.builder().build());
    }

    @Test
    public void hourlyWeatherAPIServiceImplTest() throws ExecutionException, InterruptedException {
        when(this.serviceUtil.groupsEndpoints(anyList())).thenReturn(this.endpointPropertyMap);
        when(this.client.getHourlyWeather(anyString(),anyDouble(), anyDouble(), anyString(), anyString()).get())
                .thenReturn(HourlyWeather.builder().build());
        HourlyWeather hourlyWeather = serviceImpl.getHourlyWeather(22.0, 23.2);
        assertNotNull(hourlyWeather);
        verify(this.client, times(1)).getHourlyWeather(anyString(),anyDouble(), anyDouble(), anyString(), anyString());
    }
}
