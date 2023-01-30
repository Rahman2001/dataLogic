package datalogic.controller;

import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.service.clientService.DailyWeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/current")
    @Nullable
    public DailyWeather getDailyWeatherOfCurrentLocation(@Qualifier("userLocation") final UserLocation userLocation){
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.dailyWeatherAPIClientService.getDailyWeather(
                userLocation.getLat(), userLocation.getLon()));
        return dailyWeather.isEmpty() ? null : dailyWeather.get();
    }
}
