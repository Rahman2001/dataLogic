package datalogic.controller;

import datalogic.model.UserLocation;
import datalogic.model.Weather;
import datalogic.repository.WeatherRepoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherAPI { //returns current weather data
    private final WeatherRepoServiceImpl repoService;

    @Autowired
    public WeatherAPI(final WeatherRepoServiceImpl repoService) {
        this.repoService = repoService;
    }

    @PostMapping
    public ResponseEntity<Weather> getCurrentWeatherOfCurrentLocation(@RequestBody final UserLocation userLocation) {
        boolean isAvailable = this.repoService.waitUntilAvailable(userLocation);
        Weather currentWeatherFromDb = isAvailable ? this.repoService.selectCurrentWeather(userLocation.getCity()) : null;
        return currentWeatherFromDb != null ? ResponseEntity.ok(currentWeatherFromDb) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{city}")
    public ResponseEntity<Weather> getCurrentWeather(@PathVariable("city") String city) {
        boolean isAvailable = this.repoService.waitUntilAvailable(city);
        Weather currentWeatherFromDb = isAvailable ? this.repoService.selectCurrentWeather(city) : null;
        return currentWeatherFromDb != null ? ResponseEntity.ok(currentWeatherFromDb) : ResponseEntity.badRequest().build();
    }
}
