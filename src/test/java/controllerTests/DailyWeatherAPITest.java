package controllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.controller.DailyWeatherAPI;
import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.service.serviceImpl.DailyWeatherAPIClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private String jsonContent;

    @BeforeEach
    public void createJsonContent() throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        UserLocation userLocation = UserLocation.builder().lon(23.1).lat(33.2).country("TR").city("Ankara").build();
        jsonContent = obj.writeValueAsString(userLocation);
    }

    @Test
    public void dailyWeatherAPITest() throws Exception {
        when(service.getDailyWeather(any(), any())).thenReturn(DailyWeather.builder().build());
        mockMvc.perform(get("/weather/daily").contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)).andExpect(status().isOk());
    }

    @Test
    public void dailyWeatherAPIByCity() throws Exception {
        when(service.getDailyWeather(anyString())).thenReturn(DailyWeather.builder().build());
        mockMvc.perform(get("/weather/daily/Ankara")).andExpect(status().isOk());
    }
}
