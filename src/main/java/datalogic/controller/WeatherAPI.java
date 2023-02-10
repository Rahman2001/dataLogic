package datalogic.controller;

import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.repository.WeatherRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/weather")
public class WeatherAPI { //returns current weather data
    private final WeatherRepoService repoService;

    @Autowired
    public WeatherAPI(final WeatherRepoService repoService) {
        this.repoService = repoService;
    }

    @PostMapping
    public ResponseEntity<Weather> getCurrentWeatherOfCurrentLocation(@RequestBody final UserLocation userLocation) {
        Weather currentWeatherFromDb = this.repoService.selectCurrentWeather(userLocation.getCity());
        if(currentWeatherFromDb == null) { // if current weather data taken from database does not exist in db or is out of date (not fresh), then...
            currentWeatherFromDb = this.repoService.updateOrInsertCurrentWeather(userLocation.getLat(), userLocation.getLon(), userLocation.getCity());
            return currentWeatherFromDb != null ? ResponseEntity.ok(currentWeatherFromDb) : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(currentWeatherFromDb);
    }

    @GetMapping("/{city}")
    public ResponseEntity<Weather> getCurrentWeather(@PathVariable("city") String city) {
        Weather currentWeatherFromDb = this.repoService.selectCurrentWeather(city);
        if(currentWeatherFromDb == null) { // if current weather data taken from database does not exist in db or is out of date (not fresh), then...
            currentWeatherFromDb = this.repoService.updateOrInsertCurrentWeather(city);
            return currentWeatherFromDb != null ? ResponseEntity.ok(currentWeatherFromDb) : ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(currentWeatherFromDb);
    }
}
