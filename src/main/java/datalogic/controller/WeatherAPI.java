package datalogic.controller;

import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.service.serviceImpl.WeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/weather")
public class WeatherAPI { //returns current weather data
    private final WeatherAPIClientServiceImpl weatherAPIClientService;

    @Autowired
    public WeatherAPI(final WeatherAPIClientServiceImpl weatherAPIClientService) {
        this.weatherAPIClientService = weatherAPIClientService;
    }

    @PostMapping
    public ResponseEntity<Weather> getCurrentWeatherOfCurrentLocation(@RequestBody final UserLocation userLocation) {
        Optional<Weather> current = Optional.ofNullable(this.weatherAPIClientService.getCurrentWeatherData(userLocation.getLat(), userLocation.getLon()));
        current.ifPresent(weather -> {
            weather.setCity(userLocation.getCity());
            weather.setCountry(userLocation.getCountry());
        });
        return current.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(current.get());
    }

    @GetMapping("/{city}")
    public ResponseEntity<Weather> getCurrentWeather(@PathVariable("city") String city) {
        Optional<Weather> weatherOfCity = Optional.ofNullable(this.weatherAPIClientService.getCurrentWeatherData(city));
        weatherOfCity.ifPresent(weather -> weather.setCity(city));
        return weatherOfCity.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(weatherOfCity.get());
    }
}
