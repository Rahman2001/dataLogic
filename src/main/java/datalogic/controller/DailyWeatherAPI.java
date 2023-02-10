package datalogic.controller;

import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.repository.WeatherRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/weather/daily")
public class DailyWeatherAPI { //returns daily weather forecast
    private final WeatherRepoService repoService;
    @Autowired
    public DailyWeatherAPI(final WeatherRepoService repoService){
        this.repoService = repoService;
    }

    @PostMapping
    public ResponseEntity<DailyWeather> getDailyWeatherForCurrentLocation(@RequestBody final UserLocation userLocation){
        DailyWeather dailyWeatherFromDb = this.repoService.selectDailyWeather(userLocation.getCity());
        if(dailyWeatherFromDb == null) {
            dailyWeatherFromDb = this.repoService.updateOrInsertDailyWeather(userLocation.getLat(), userLocation.getLon(), userLocation.getCity());
            return dailyWeatherFromDb != null ? ResponseEntity.ok(dailyWeatherFromDb) : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dailyWeatherFromDb);
    }

    @GetMapping("/{city}")
    public ResponseEntity<DailyWeather> getDailyWeatherForCity(@PathVariable("city") String city) {
        DailyWeather dailyWeatherFromDb = this.repoService.selectDailyWeather(city);
        if(dailyWeatherFromDb == null) {
            dailyWeatherFromDb = this.repoService.updateOrInsertDailyWeather(city);
            return dailyWeatherFromDb != null ? ResponseEntity.ok(dailyWeatherFromDb) : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dailyWeatherFromDb);
    }
}
