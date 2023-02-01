package controllerTests;

import datalogic.controller.HourlyWeatherAPI;
import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.service.serviceImpl.HourlyWeatherAPIClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = {HourlyWeatherAPI.class})
@ContextConfiguration(classes = {HourlyWeatherAPI.class})
public class HourlyWeatherAPITest {
    @MockBean
    private HourlyWeatherAPIClientServiceImpl service;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void hourlyWeatherAPITest() throws Exception {
        when(service.getHourlyWeather(any(),any())).thenReturn(HourlyWeather.builder().build());
        mockMvc.perform(get("/weather/hourly/currentLocation")).andExpect(status().isOk());
    }
}
