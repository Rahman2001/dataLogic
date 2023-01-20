package datalogic.controller;

import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.service.IP_APIClientServiceImpl;
import datalogic.service.WeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/weather")
public class WeatherAPI { //returns current, hourly and daily weather data
    private final WeatherAPIClientServiceImpl weatherAPIClientService;
    private final UserLocation userLocation;

    @Autowired
    public WeatherAPI(final WeatherAPIClientServiceImpl weatherAPIClientService,
                      final IP_APIClientServiceImpl ip_apiClientService) {
        this.weatherAPIClientService = weatherAPIClientService;
        this.userLocation = ip_apiClientService.getUserLocation();
    }

    @GetMapping("/current")
    @Nullable
    public Weather getCurrentWeatherOfCurrentWeather() {
        Optional<Weather> current = Optional.ofNullable(this.weatherAPIClientService.getCurrentWeatherData(this.userLocation.getLat(), this.userLocation.getLon()));
        return current.isEmpty() ? null : current.get();
    }

}
