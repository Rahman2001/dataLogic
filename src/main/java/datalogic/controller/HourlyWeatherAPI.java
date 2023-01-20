package datalogic.controller;

import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/weather/hourly")
public class HourlyWeatherAPI { //returns hourly weather forecast
    private final HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientService;
    private final UserLocation userLocation;

    @Autowired
    public HourlyWeatherAPI(final IP_APIClientServiceImpl ip_apiClientService,
                            final HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientServiceImpl){
        this.hourlyWeatherAPIClientService = hourlyWeatherAPIClientServiceImpl;
        this.userLocation = ip_apiClientService.getUserLocation();
    }

    @GetMapping("/current")
    @Nullable
    public HourlyWeather getHourlyWeatherOFCurrentLocation(){
        Optional<HourlyWeather> hourlyWeather = Optional.ofNullable(this.hourlyWeatherAPIClientService.getHourlyWeather(
                this.userLocation.getLat(), this.userLocation.getLon()));
        return hourlyWeather.isEmpty() ? null : hourlyWeather.get();
    }
}