package datalogic.repository;

import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.UserLocation;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class WeatherRepoServiceImpl {
    private final WeatherRepoService weatherRepoService;
    private final WeatherDataUpdateService weatherDataUpdateService;

    @Autowired
    public WeatherRepoServiceImpl(final WeatherRepoService weatherRepoService,
                                  final WeatherDataUpdateService weatherDataUpdateService) {
        this.weatherRepoService = weatherRepoService;
        this.weatherDataUpdateService = weatherDataUpdateService;
    }
    //below methods are used currently, if you want more detailed database operations, feel free to build using WeatherRepo.
    public Boolean waitUntilAvailable(String city) {
        boolean isAvailable = false;
        if(!this.weatherRepoService.exists(city)) {
            boolean isInserted = this.weatherDataUpdateService.insertedAll(city);
            if(isInserted) {
                log.info("\n---All weather data are inserted and ready for use: " + isInserted);
                this.weatherDataUpdateService.putCityInQueueForUpdate(city, LocalDateTime.now());
                isAvailable = isInserted;
            }else {
                log.error("\n--Some error occurred during insertion!--\n");
            }
        }
        else {
            log.info("\n---No need for insertion of weather data! They already exist in database!---\n");
            isAvailable = true;
        }
        return isAvailable;
    }
    public Boolean waitUntilAvailable(@NotNull UserLocation userLocation) {
        boolean isAvailable = false;
        if(!this.weatherRepoService.exists(userLocation.getCity())) {
            boolean isInserted = this.weatherDataUpdateService.insertedAll(userLocation);
            if(isInserted) {
                log.info("\n---All weather data are inserted and ready for use: " + isInserted);
                this.weatherDataUpdateService.putCityInQueueForUpdate(userLocation.getCity(), LocalDateTime.now());
                isAvailable = isInserted;
            }else {
                log.error("\n--Some error occurred during insertion!--\n");
            }
        }
        else {
            log.info("\n---No need for insertion of weather data! They already exist in database!---\n");
            isAvailable = true;
        }
        return isAvailable;
    }

    public Weather selectCurrentWeather(@NotNull String city) {
        return this.weatherRepoService.selectCurrentWeather(city).join();
    }
    public HourlyWeather selectHourlyWeather(@NotNull String city) {
        return this.weatherRepoService.selectHourlyWeather(city).join();
    }
    public DailyWeather selectDailyWeather(@NotNull String city) {
        return this.weatherRepoService.selectDailyWeather(city).join();
    }

}
