package datalogic.controller;

import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.service.serviceImpl.IP_APIClientServiceImpl;
import datalogic.service.serviceImpl.WeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequestMapping("/weather")
public class WeatherAPI { //returns current weather data
    private final WeatherAPIClientServiceImpl weatherAPIClientService;
    private final UserLocation userLocation;

    @Autowired
    public WeatherAPI(final WeatherAPIClientServiceImpl weatherAPIClientService,
                      final IP_APIClientServiceImpl ip_apiClientService) {
        this.weatherAPIClientService = weatherAPIClientService;
        this.userLocation = ip_apiClientService.getUserLocation();
    }

    @GetMapping("/currentLocation")
    public ResponseEntity<Weather> getCurrentWeatherOfCurrentWeather() {
        Optional<Weather> current = Optional.ofNullable(this.weatherAPIClientService.getCurrentWeatherData(this.userLocation.getLat(), this.userLocation.getLon()));
        current.ifPresent(weather -> {weather.setCity(this.userLocation.getCity());
                weather.setCountry(this.userLocation.getCountry());});
        return current.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(current.get());

    }

    @GetMapping("/{city}")
    public ResponseEntity<Weather> getCurrentWeather(@PathVariable("city") String city) {
        Optional<Weather> weatherOfCity = Optional.ofNullable(this.weatherAPIClientService.getCurrentWeatherData(city));
        weatherOfCity.ifPresent(weather -> weather.setCity(city));
        return weatherOfCity.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(weatherOfCity.get());
    }

    @Bean
    @Primary
    @PostConstruct
    public UserLocation userLocation(){
        return this.userLocation;
    }
}
