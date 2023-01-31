package datalogic.controller;

import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.service.serviceImpl.DailyWeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/weather/daily")
public class DailyWeatherAPI { //returns daily weather forecast
    private final DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService;

    @Autowired
    public DailyWeatherAPI(final DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService){
        this.dailyWeatherAPIClientService = dailyWeatherAPIClientService;
    }

    @GetMapping("/currentLocation")
    public ResponseEntity<DailyWeather> getDailyWeatherOfCurrentLocation(@Qualifier("userLocation") final UserLocation userLocation){
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.dailyWeatherAPIClientService.getDailyWeather(
                userLocation.getLat(), userLocation.getLon()));
        return dailyWeather.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(dailyWeather.get());
    }

    @GetMapping("/{city}")
    public ResponseEntity<DailyWeather> getDailyWeather(@PathVariable("city") String city) {
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.dailyWeatherAPIClientService.getDailyWeather(city));
        dailyWeather.ifPresent(weather -> weather.setCity(city));
        return dailyWeather.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(dailyWeather.get());
    }
}
