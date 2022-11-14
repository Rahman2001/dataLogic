package datalogic.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.config.FlywayConfig;
import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import datalogic.service.FlywayMigrationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
public class WeatherRepo {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private FlywayConfig flywayConfig;
    @Autowired
    private FlywayMigrationService flywayMigrationService;

    public Boolean createHourlyWeather(@NotNull HourlyWeather hourlyWeather) {
        String query = "CREATE TABLE HourlyWeather_of_" + hourlyWeather.getCity() +
                "(city VARCHAR(25) NOT NULL PRIMARY KEY, " +
                "date_and_time DATETIME default NOW(), " +
                "temp INT, " +
                "temp_min INT, " +
                "temp_max INT, " +
                "pressure INT, " +
                "humidity INT, " +
                "wind INT, " +
                "feels_like INT, " +
                "clouds INT, " +
                "CONSTRAINT fk_key FOREIGN KEY(city) REFERENCES locations(city))";
        return this.isTableCreated(query);
    }

    public Boolean createDailyWeather(@NotNull DailyWeather dailyWeather) {
        String query = "CREATE TABLE DailyWeather_of_" + dailyWeather.getCity() +
                "(city VARCHAR(25) NOT NULL PRIMARY KEY, " +
                "date_and_time DATETIME default NOW(), " +
                "temp INT, " +
                "temp_min INT, " +
                "temp_max INT, " +
                "pressure INT, " +
                "humidity INT, " +
                "wind INT, " +
                "feels_like INT, " +
                "clouds INT, " +
                "CONSTRAINT fk_key FOREIGN KEY(city) REFERENCES locations(city))";
        return this.isTableCreated(query);
    }

    public Boolean insertLocation(@NotNull String cityName, @NotNull String countryName) {
        String query = "INSERT INTO locations " +
                "(city, country) " +
                "VALUES (:city, :country)";

        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", cityName);
        locationMap.put("country", countryName);

        return this.isInsertedAndModified(query, locationMap);
    }

    public Boolean insertCurrentWeather(@NotNull Weather currentWeather) {
        String query = "INSERT INTO current_weather " +
                "(description, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds, city, country) " +
                "VALUES (:description, :temp, :temp_min, :temp_max, :pressure, :humidity, :wind, :feels_like, :clouds, :city, :country)";

        Map weatherMap = new ObjectMapper().convertValue(currentWeather, Map.class);
        return this.isInsertedAndModified(query, weatherMap);
    }

    public Boolean insertHourlyWeather(@NotNull HourlyWeather hourlyWeather) {
        String query = "INSERT INTO HourlyWeather_of_" + hourlyWeather.getCity() +
                " (date_and_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
                "VALUES (:date_and_time, :temp, :temp_min, :temp_max, :pressure, :humidity, :wind, :feels_like, :clouds)";

        List<Map>  mapList = hourlyWeather.getHourly_weather().stream()
                .map(weather -> new ObjectMapper().convertValue(weather, Map.class)).toList();
        boolean[] isInsertedAndCreated = new boolean[2];
        isInsertedAndCreated[0] = this.isInserted(query, mapList);
        isInsertedAndCreated[1] = this.flywayMigrationService.insertDataIntoFile(query);

        return isInsertedAndCreated[0] && isInsertedAndCreated[1];
    }

    public Boolean insertDailyWeather(DailyWeather dailyWeather) {
        String query = "INSERT INTO DailyWeather_of_" + dailyWeather.getCity() +
                " (date_and_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
                "VALUES (:date_and_time, :temp, :temp_min, :temp_max, :pressure, :humidity, :wind, :feels_like, :clouds)";

        List<Map>  mapList = dailyWeather.getDaily_weather().stream()
                .map(weather -> new ObjectMapper().convertValue(weather, Map.class)).toList();
        boolean[] isInsertedAndModified = new boolean[2];
        isInsertedAndModified[0] = this.isInserted(query, mapList);
        isInsertedAndModified[1] = this.flywayMigrationService.insertDataIntoFile(query);

        return isInsertedAndModified[0] && isInsertedAndModified[1];
    }

    @NotNull
    private Boolean isInserted(String query, List<Map> mapList) {
        List<MapSqlParameterSource> mapSqlParameterSourceList = mapList.stream().map(MapSqlParameterSource::new).toList();

        int totalWeatherNumber = mapList.size();
        AtomicInteger insertedWeatherNumber = new AtomicInteger(0);

        mapSqlParameterSourceList.forEach(mapSqlParameterSource -> {
            insertedWeatherNumber.addAndGet(this.namedParameterJdbcTemplate.update(query, mapSqlParameterSource));
        });

        return insertedWeatherNumber.get() == totalWeatherNumber;
    }
    @NotNull
    private Boolean isInsertedAndModified(String query, Map<String, String> locationMap) {
        boolean[] isUpdatedAndModified = new boolean[2];
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        isUpdatedAndModified[0] = this.namedParameterJdbcTemplate.update(query, parameterSource) == 1;
        isUpdatedAndModified[1] = this.flywayMigrationService.insertDataIntoFile(query);

        return isUpdatedAndModified[0] && isUpdatedAndModified[1];
    }

    @NotNull
    private Boolean isTableCreated(String query) {
        boolean executedSuccessfully = false;

        try(Connection connection = this.flywayConfig.dataSource().getConnection()) {
            Statement sqlStatement = connection.createStatement();
            executedSuccessfully = sqlStatement.execute(query);
            sqlStatement.close();
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return executedSuccessfully;
    }
}
