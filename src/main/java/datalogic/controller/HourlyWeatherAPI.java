package datalogic.controller;

import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.service.serviceImpl.HourlyWeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/weather/hourly")
public class HourlyWeatherAPI { //returns hourly weather forecast
    private final HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientService;

    @Autowired
    public HourlyWeatherAPI(final HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientServiceImpl){
        this.hourlyWeatherAPIClientService = hourlyWeatherAPIClientServiceImpl;
    }

    @PostMapping
    public ResponseEntity<HourlyWeather> getHourlyWeatherOfCurrentLocation(@RequestBody final UserLocation userLocation){
        Optional<HourlyWeather> hourlyWeather = Optional.ofNullable(this.hourlyWeatherAPIClientService.getHourlyWeather(
                userLocation.getLat(), userLocation.getLon()));
        return hourlyWeather.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(hourlyWeather.get());
    }

    @GetMapping("/{city}")
    public ResponseEntity<HourlyWeather> getHourlyWeather(@PathVariable("city") String city) {
        Optional<HourlyWeather> hourlyWeather = Optional.ofNullable(this.hourlyWeatherAPIClientService.getHourlyWeather(city));
        hourlyWeather.ifPresent(weather -> weather.setCity(city));
        return hourlyWeather.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(hourlyWeather.get());
    }
}
