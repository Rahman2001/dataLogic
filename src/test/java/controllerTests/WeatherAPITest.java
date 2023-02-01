package controllerTests;

import datalogic.controller.WeatherAPI;
import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.service.serviceImpl.IP_APIClientServiceImpl;
import datalogic.service.serviceImpl.WeatherAPIClientServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private IP_APIClientServiceImpl ipService;
    static UserLocation userLocation;

    @BeforeAll
    public static void createUserLocation() {
        userLocation = UserLocation.builder().city("Ankara").country("Turkey").lat(29.222).lon(35.212).build();
    }

    @Test
    public void weatherAPITest() throws Exception {
        doReturn(userLocation).when(ipService).getUserLocation();
        when(clientService.getCurrentWeatherData(anyDouble(), anyDouble())).thenReturn(Weather.builder().wind(22).description("cold").build());
        mockMvc.perform(get("/weather/currentLocation")).andExpect(status().isOk());
    }

}
