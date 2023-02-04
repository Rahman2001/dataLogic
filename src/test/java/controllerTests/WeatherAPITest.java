package controllerTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.controller.WeatherAPI;
import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.service.serviceImpl.WeatherAPIClientServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {WeatherAPI.class})
@ContextConfiguration(classes = {WeatherAPI.class})
@ExtendWith(MockitoExtension.class)
public class WeatherAPITest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WeatherAPIClientServiceImpl clientService;
    static String json;

    @BeforeAll
    public static void createUserLocation() throws JsonProcessingException {
        UserLocation userLocation = UserLocation.builder().city("Ankara").country("Turkey")
                .lat(29.222).lon(35.212).build();
        json = new ObjectMapper().writeValueAsString(userLocation);
    }

    @Test
    public void weatherAPITest() throws Exception {
        when(clientService.getCurrentWeatherData(anyDouble(), anyDouble())).thenReturn(Weather.builder().wind(22).description("cold").build());
        mockMvc.perform(get("/weather").contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isOk());
    }

}
