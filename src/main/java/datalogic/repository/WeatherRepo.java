package datalogic.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
public class WeatherRepo {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Boolean insertLocation(String cityName, String countryName) {
        String query = "INSERT INTO locations " +
                "(city, country) " +
                "VALUES (:city, :country)";

        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", cityName);
        locationMap.put("country", countryName);

        return this.namedParameterJdbcTemplate.update(query, locationMap) == 1;
    }

    public Boolean insertCurrentWeather(Weather currentWeather) {
        String query = "INSERT INTO current_weather " +
                "(description, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds, country) " +
                "VALUES (:description, :temp, :temp_min, :temp_max, :pressure, :humidity, :wind, :feels_like, :clouds, :country)";

        Map weatherMap = new ObjectMapper().convertValue(currentWeather, Map.class);

        MapSqlParameterSource parameterSource = new MapSqlParameterSource(weatherMap);

        return this.namedParameterJdbcTemplate.update(query, parameterSource) == 1;
    }

    public Boolean insertHourlyWeather(HourlyWeather hourlyWeather, String cityName) {
        String query = "INSERT INTO hourly_weather_of_" + cityName +
                " (date_and_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
                "VALUES (:date_and_time, :temp, :temp_min, :temp_max, :pressure, :humidity, :wind, :feels_like, :clouds)";

        List<Map>  mapList = hourlyWeather.getHourly_weather().stream()
                .map(weather -> new ObjectMapper().convertValue(weather, Map.class)).toList();

        return getaBoolean(query, mapList);
    }

    public Boolean insertDailyWeather(DailyWeather dailyWeather, String cityName) {
        String query = "INSERT INTO daily_weather_of_" + cityName +
                " (date_and_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
                "VALUES (:date_and_time, :temp, :temp_min, :temp_max, :pressure, :humidity, :wind, :feels_like, :clouds)";

        List<Map>  mapList = dailyWeather.getDaily_weather().stream()
                .map(weather -> new ObjectMapper().convertValue(weather, Map.class)).toList();

        return getaBoolean(query, mapList);
    }

    @NotNull
    private Boolean getaBoolean(String query, List<Map> mapList) {
        List<MapSqlParameterSource> mapSqlParameterSourceList = mapList.stream().map(MapSqlParameterSource::new).toList();

        int totalWeatherNumber = mapList.size();
        AtomicInteger insertedWeatherNumber = new AtomicInteger(0);

        mapSqlParameterSourceList.forEach(mapSqlParameterSource -> {
            insertedWeatherNumber.addAndGet(this.namedParameterJdbcTemplate.update(query, mapSqlParameterSource));
        });

        return insertedWeatherNumber.get() == totalWeatherNumber;
    }
}
