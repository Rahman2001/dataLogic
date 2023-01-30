package datalogic.controller;

import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.service.clientService.HourlyWeatherAPIClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/weather/hourly")
public class HourlyWeatherAPI { //returns hourly weather forecast
    private final HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientService;

    @Autowired
    public HourlyWeatherAPI(final HourlyWeatherAPIClientServiceImpl hourlyWeatherAPIClientServiceImpl){
        this.hourlyWeatherAPIClientService = hourlyWeatherAPIClientServiceImpl;
    }

    @GetMapping("/current")
    @Nullable
    public HourlyWeather getHourlyWeatherOFCurrentLocation(@Qualifier("userLocation") final UserLocation userLocation){
        Optional<HourlyWeather> hourlyWeather = Optional.ofNullable(this.hourlyWeatherAPIClientService.getHourlyWeather(
                userLocation.getLat(), userLocation.getLon()));
        return hourlyWeather.isEmpty() ? null : hourlyWeather.get();
    }
}
