package controllerTests;

import datalogic.controller.DailyWeatherAPI;
import datalogic.model.DailyWeather;
import datalogic.service.serviceImpl.DailyWeatherAPIClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = {DailyWeatherAPI.class})
@ContextConfiguration(classes = {DailyWeatherAPI.class})
public class DailyWeatherAPITest {
    @MockBean
    private DailyWeatherAPIClientServiceImpl service;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void dailyWeatherAPITest() throws Exception {
        when(service.getDailyWeather(any(), any())).thenReturn(DailyWeather.builder().build());
        mockMvc.perform(get("/weather/daily/currentLocation")).andExpect(status().isOk());
    }
}
