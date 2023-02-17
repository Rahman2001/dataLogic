package serviceTests.serviceImplTests;

import datalogic.config.EndpointProperty;
import datalogic.model.Weather;
import datalogic.service.ServiceUtil;
import datalogic.service.clientService.WeatherAPIClientService;
import datalogic.service.serviceImpl.ApiServiceUtil;
import datalogic.service.serviceImpl.WeatherAPIClientServiceImpl;
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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ApiServiceUtil.class})
public class WeatherAPIServiceImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WeatherAPIClientService client;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServiceUtil serviceUtil;
    private Map<String, EndpointProperty> endpointPropertyMap;

    @InjectMocks
    WeatherAPIClientServiceImpl serviceImpl;

    @BeforeEach
    public void createClient() {
        this.endpointPropertyMap = new HashMap<>();
        this.endpointPropertyMap.put("rahman", EndpointProperty.builder().build());
    }

    @Test
    public void weatherAPIServiceImplTest() throws ExecutionException, InterruptedException {
        when(this.serviceUtil.groupsEndpoints(anyList())).thenReturn(this.endpointPropertyMap);
        when(this.client.getWeatherData(anyString(), anyDouble(), anyDouble(), anyString(), anyString()).get())
                .thenReturn(Weather.builder().build());
        Weather weather = this.serviceImpl.getCurrentWeatherData(23.0, 22.11);
        assertNotNull(weather);
        verify(this.client, times(1)).getWeatherData(anyString(), anyDouble(), anyDouble(), anyString(), anyString());
    }

}
