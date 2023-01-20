package datalogic.controller;

import datalogic.model.DailyWeather;
import datalogic.model.UserLocation;
import datalogic.service.DailyWeatherAPIClientServiceImpl;
import datalogic.service.IP_APIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/weather/daily")
public class DailyWeatherAPI { //returns daily weather forecast
    private final UserLocation userLocation;
    private final DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService;

    @Autowired
    public DailyWeatherAPI(final IP_APIClientServiceImpl ip_apiClientService,
                           final DailyWeatherAPIClientServiceImpl dailyWeatherAPIClientService){
        this.userLocation = ip_apiClientService.getUserLocation();
        this.dailyWeatherAPIClientService = dailyWeatherAPIClientService;
    }

    @GetMapping("/current")
    @Nullable
    public DailyWeather getDailyWeatherOfCurrentLocation(){
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.dailyWeatherAPIClientService.getDailyWeather(
                this.userLocation.getLat(), this.userLocation.getLon()));
        return dailyWeather.isEmpty() ? null : dailyWeather.get();
    }
}
