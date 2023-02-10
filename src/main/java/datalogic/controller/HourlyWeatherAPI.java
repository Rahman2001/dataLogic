package datalogic.controller;

import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.repository.WeatherRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather/hourly")
public class HourlyWeatherAPI { //returns hourly weather forecast
    private final WeatherRepoService repoService;

    @Autowired
    public HourlyWeatherAPI(final WeatherRepoService repoService){
        this.repoService = repoService;
    }

    @PostMapping
    public ResponseEntity<HourlyWeather> getHourlyWeatherOfCurrentLocation(@RequestBody final UserLocation userLocation){
        HourlyWeather hourlyWeatherFromDb = this.repoService.selectHourlyWeather(userLocation.getCity());
        if(hourlyWeatherFromDb == null) {
            hourlyWeatherFromDb = this.repoService.updateOrInsertHourlyWeather(userLocation.getLat(), userLocation.getLon(), userLocation.getCity());
            return hourlyWeatherFromDb != null ? ResponseEntity.ok(hourlyWeatherFromDb) : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(hourlyWeatherFromDb);
    }

    @GetMapping("/{city}")
    public ResponseEntity<HourlyWeather> getHourlyWeather(@PathVariable("city") String city) {
        HourlyWeather hourlyWeatherFromDb = this.repoService.selectHourlyWeather(city);
        if(hourlyWeatherFromDb == null) {
            hourlyWeatherFromDb = this.repoService.updateOrInsertHourlyWeather(city);
            return hourlyWeatherFromDb != null ? ResponseEntity.ok(hourlyWeatherFromDb) : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(hourlyWeatherFromDb);
    }
}
