package datalogic.controller;

import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.service.serviceImpl.DailyWeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/weather/daily")
public class DailyWeatherAPI { //returns daily weather forecast
    private final DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService;

    @Autowired
    public DailyWeatherAPI(final DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService){
        this.dailyWeatherAPIClientService = dailyWeatherAPIClientService;
    }

    @PostMapping
    public ResponseEntity<DailyWeather> getDailyWeatherForCurrentLocation(@RequestBody final UserLocation userLocation){
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.dailyWeatherAPIClientService.getDailyWeather(
                userLocation.getLat(), userLocation.getLon()));
        return dailyWeather.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(dailyWeather.get());
    }

    @GetMapping("/{city}")
    public ResponseEntity<DailyWeather> getDailyWeatherForCity(@PathVariable("city") String city) {
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.dailyWeatherAPIClientService.getDailyWeather(city));
        dailyWeather.ifPresent(weather -> weather.setCity(city));
        return dailyWeather.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(dailyWeather.get());
    }
}
