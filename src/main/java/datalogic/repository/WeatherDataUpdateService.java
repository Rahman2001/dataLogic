package datalogic.repository;

import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import datalogic.service.serviceImpl.ApiServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
/*
This service automatically updates/inserts weather data into database by
 using SchedulingConfigurer interface in TaskSchedulerConfig.java
All needed configurations are in TaskSchedulerConfig.java
 */
@Service
@Slf4j
public class WeatherDataUpdateService implements Runnable{

    private String databaseLocation;
    private final WeatherRepoService weatherRepoService;
    private final static Queue<Map<String, LocalDateTime>> citiesToUpdate = new ConcurrentLinkedQueue<>();
    private final ApiServiceUtil apiServiceUtil;
    @Autowired
    public WeatherDataUpdateService(final WeatherRepoService weatherRepoService,
                                    final ApiServiceUtil apiServiceUtil,
                                    @Value("${spring.database.geolocation}") final String databaseLocation) {
        this.weatherRepoService = weatherRepoService;
        this.apiServiceUtil = apiServiceUtil;
        this.databaseLocation = databaseLocation;
        if(citiesToUpdate.isEmpty() && !this.weatherRepoService.exists(databaseLocation)) {
            this.insertedAll(databaseLocation);
        }else {
            setQueueMap();
        }
        log.info("Database geolocation : " + this.databaseLocation);
    }

    public void updateDataBySchedule() {
        Map<String, LocalDateTime> cityToUpdate = this.getCityAndTimeToUpdate();
        AtomicReference<Map<String, ? extends Weather>> weathers = new AtomicReference<>();
        cityToUpdate.forEach((city, dateTime)-> weathers.set(this.loadWeathersFromExternalProvider(city)));
        boolean isUpdated = this.updatedAll(weathers.get());
        log.info("Successfully updated all weather data in database: " + isUpdated);
    }
    private Map<String, ? extends Weather> loadWeathersFromExternalProvider(String city) {
        Map<String, ? extends Weather> weathers = this.apiServiceUtil.callAll(city);
        return getStringMap(city, weathers);
    }
    private Map<String, ? extends Weather> loadWeathersFromExternalProvider(Double lat, Double lon, String city) {
        Map<String, ? extends Weather> weathers = this.apiServiceUtil.callAll(lat, lon);
        return getStringMap(city, weathers);
    }

    @NotNull
    private Map<String, ? extends Weather> getStringMap(String city, Map<String, ? extends Weather> weathers) {
        Weather currentWeather = weathers.get(Weather.class.getName());
        currentWeather.setCity(city);
        String country = currentWeather.getCountry();

        HourlyWeather hourlyWeather = (HourlyWeather) weathers.get(HourlyWeather.class.getName());
        List<Weather> list = hourlyWeather.getHourlyWeatherList();
        list.forEach(w-> {
            w.setCity(city);
            w.setCountry(country);
        });
        hourlyWeather.setHourlyWeatherList(list);
        hourlyWeather.setCity(city);
        hourlyWeather.setCountry(country);

        DailyWeather dailyWeather = (DailyWeather) weathers.get(DailyWeather.class.getName());
        list = dailyWeather.getDailyWeatherList();
        list.forEach(w-> {
            w.setCity(city);
            w.setCountry(country);
        });
        dailyWeather.setDailyWeatherList(list);
        dailyWeather.setCity(city);
        dailyWeather.setCountry(country);
        return weathers;
    }

    private Boolean updatedAll(Map<String, ? extends Weather> weatherMap) {
        Weather currentWeather = weatherMap.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) weatherMap.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) weatherMap.get(DailyWeather.class.getName());

        return this.weatherRepoService.updatedAllWeathers(currentWeather, hourlyWeather, dailyWeather);
    }
    public Boolean insertedAll(String city) {
        Map<String, ? extends Weather> map = this.loadWeathersFromExternalProvider(city);
        Weather currentWeather = map.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) map.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) map.get(DailyWeather.class.getName());
        return this.weatherRepoService.insertedAllWeather(currentWeather, hourlyWeather, dailyWeather);
    }
    public Boolean insertedAll(Double lat, Double lon, String city) {
        Map<String, ? extends Weather> map = this.loadWeathersFromExternalProvider(lat, lon, city);
        Weather currentWeather = map.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) map.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) map.get(DailyWeather.class.getName());

        return this.weatherRepoService.insertedAllWeather(currentWeather, hourlyWeather, dailyWeather);
    }
    public void putCityInQueueForUpdate(String city, LocalDateTime currentTime) { // puts any data into the Queue for data update via scheduling.
        if (this.weatherRepoService.exists(city)) {
            Map<String, LocalDateTime> cityForUpdate = new HashMap<>();
            cityForUpdate.put(city, currentTime);
            citiesToUpdate.removeIf(map-> map.containsKey(city));
            citiesToUpdate.add(cityForUpdate);
        }else {
            log.error("\n--- Could not find " + city + " in locations table! ---\n");
        }
    }
    public Queue<Map<String, LocalDateTime>> getCitiesForSchedule() {
        if(citiesToUpdate.isEmpty()) {
            setQueueMap();
        }
        return new ArrayDeque<>(citiesToUpdate);
    }
    private Map<String, LocalDateTime> getCityAndTimeToUpdate() {
        if(citiesToUpdate.isEmpty()) {
            setQueueMap();
        }
        return citiesToUpdate.poll();
    }
    private void setQueueMap() { // loads data from database. Should be used only if we are sure any data exists in database
        SqlRowSet rs = this.weatherRepoService.selectAllLocations();
        while((rs.isBeforeFirst() && rs.first()) || !rs.isAfterLast()) {
            this.putCityInQueueForUpdate(rs.getString("city"),
                    Objects.requireNonNull((LocalDateTime) rs.getObject("updated_time")));
            rs.next();
        }
    }

    @Override
    public void run() {
        this.updateDataBySchedule();
    }
}
