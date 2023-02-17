package datalogic.controller;

import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.repository.WeatherRepoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather/daily")
public class DailyWeatherAPI { //returns daily weather forecast
    private final WeatherRepoServiceImpl repoService;
    @Autowired
    public DailyWeatherAPI(final WeatherRepoServiceImpl repoService){
        this.repoService = repoService;
    }

    @PostMapping
    public ResponseEntity<DailyWeather> getDailyWeatherForCurrentLocation(@RequestBody final UserLocation userLocation){
        boolean isAvailable = this.repoService.waitUntilAvailable(userLocation.getCity(), userLocation.getLat(), userLocation.getLon());
        DailyWeather dailyWeather = isAvailable ? this.repoService.selectDailyWeather(userLocation.getCity()) : null;
        return dailyWeather != null ? ResponseEntity.ok(dailyWeather) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{city}")
    public ResponseEntity<DailyWeather> getDailyWeatherForCity(@PathVariable("city") String city) {
        boolean isAvailable = this.repoService.waitUntilAvailable(city);
        DailyWeather dailyWeather = isAvailable ? this.repoService.selectDailyWeather(city) : null;
        return dailyWeather != null ? ResponseEntity.ok(dailyWeather) : ResponseEntity.badRequest().build();
    }
}
