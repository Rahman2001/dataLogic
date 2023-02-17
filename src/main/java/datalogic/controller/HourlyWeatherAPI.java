package datalogic.controller;

import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.repository.WeatherRepoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather/hourly")
public class HourlyWeatherAPI { //returns hourly weather forecast
    private final WeatherRepoServiceImpl repoService;

    @Autowired
    public HourlyWeatherAPI(final WeatherRepoServiceImpl repoService){
        this.repoService = repoService;
    }

    @PostMapping
    public ResponseEntity<HourlyWeather> getHourlyWeatherOfCurrentLocation(@RequestBody final UserLocation userLocation){
       boolean isAvailable = this.repoService.waitUntilAvailable(userLocation.getCity(), userLocation.getLat(), userLocation.getLon());
       HourlyWeather hourlyWeather = isAvailable ? this.repoService.selectHourlyWeather(userLocation.getCity()) : null;
       return hourlyWeather != null ? ResponseEntity.ok(hourlyWeather) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/{city}")
    public ResponseEntity<HourlyWeather> getHourlyWeather(@PathVariable("city") String city) {
        boolean isAvailable = this.repoService.waitUntilAvailable(city);
        HourlyWeather hourlyWeather = isAvailable ? this.repoService.selectHourlyWeather(city) : null;
        return hourlyWeather != null ? ResponseEntity.ok(hourlyWeather) : ResponseEntity.badRequest().build();
    }
}
