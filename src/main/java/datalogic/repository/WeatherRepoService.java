package datalogic.repository;

import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import datalogic.repository.rowMappers.DailyWeatherRowMapper;
import datalogic.repository.rowMappers.HourlyWeatherRowMapper;
import datalogic.repository.rowMappers.WeatherRowMapper;
import datalogic.service.FlywayMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class WeatherRepoService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FlywayMigrationService flywayMigrationService;

    @Autowired
    public WeatherRepoService(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              FlywayMigrationService flywayMigrationService){
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.flywayMigrationService = flywayMigrationService;
    }

    @Async
    public CompletableFuture<Weather> selectCurrentWeather(@NotNull String city) {
        String queryForCurrentWeather = "CALL selectCurrentWeatherIfUpdate(?)";
        Optional<Weather> weather = Optional.ofNullable(this.namedParameterJdbcTemplate.getJdbcTemplate()
                .queryForObject(queryForCurrentWeather, new WeatherRowMapper(), city));
        return CompletableFuture.completedFuture(weather.orElse(null));
    }
    @Async
    public CompletableFuture<HourlyWeather> selectHourlyWeather(@NotNull String city) {
        String queryForHourlyWeather = "CALL selectHourlyWeatherIfUpdate(?)";
        Optional<HourlyWeather> hourlyWeather = Optional.ofNullable(this.namedParameterJdbcTemplate.getJdbcTemplate()
                .queryForObject(queryForHourlyWeather, new HourlyWeatherRowMapper(), city));
        return CompletableFuture.completedFuture(hourlyWeather.orElse(null));
    }
    @Async
    public CompletableFuture<DailyWeather> selectDailyWeather(@NotNull String city) {
        String queryForDailyWeather = "CALL selectDailyWeatherIfUpdate(?)";
        Optional<DailyWeather> dailyWeather = Optional.ofNullable(this.namedParameterJdbcTemplate.getJdbcTemplate()
                .queryForObject(queryForDailyWeather, new DailyWeatherRowMapper(), city));
        return CompletableFuture.completedFuture(dailyWeather.orElse(null));
    }
    public SqlRowSet selectAllLocations() {
        String query = "SELECT city, updated_time FROM locations ORDER BY updated_time";
        return this.namedParameterJdbcTemplate.getJdbcOperations().queryForRowSet(query);
    }
    @SuppressWarnings("all")
    public Boolean exists(@NotNull String city) {
        String query = "SELECT EXISTS (SELECT city FROM locations WHERE city = :city) AS isTrue";
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", city);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        return this.namedParameterJdbcTemplate.execute(query, parameterSource, ps -> {
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("isTrue") : -1;
        }) > 0;
    }
    public Boolean insertedAllWeather(@NotNull Weather weather, @NotNull HourlyWeather hourlyWeather, @NotNull DailyWeather dailyWeather) {
        Boolean isLocationInserted = this.insertedLocation(weather.getCity(), weather.getCountry());
        CompletableFuture<Boolean> currentWeatherInserted = CompletableFuture.supplyAsync(() -> this.insertedCurrentWeather(weather));
        CompletableFuture<Boolean> hourlyWeatherInserted = CompletableFuture.supplyAsync(() -> this.insertedHourlyWeather(hourlyWeather));
        CompletableFuture<Boolean> dailyWeatherInserted = CompletableFuture.supplyAsync(() -> this.insertedDailyWeather(dailyWeather));
        List<CompletableFuture<Boolean>> areAllInserted = List.of(currentWeatherInserted, hourlyWeatherInserted, dailyWeatherInserted);
        return isLocationInserted && CompletableFuture.allOf(areAllInserted.toArray(CompletableFuture[]::new)).
                thenApply(f -> areAllInserted.stream().map(CompletableFuture::join).filter(isInserted ->
                        isInserted.booleanValue() == Boolean.TRUE).count())
                .join().intValue() == areAllInserted.size();
    }
    public Boolean insertedCurrentWeather(@NotNull Weather currentWeather) {
        String query = "INSERT INTO Current_Weather " +
                "(date_time, description, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds, city, country) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] values = new Object[] {
                currentWeather.getDateTime(), currentWeather.getDescription(), currentWeather.getTemp(),
                currentWeather.getTempMin(), currentWeather.getTempMax(), currentWeather.getPressure(),
                currentWeather.getHumidity(), currentWeather.getWind(), currentWeather.getFeelsLike(),
                currentWeather.getClouds(), currentWeather.getCity(), currentWeather.getCountry()};
        int updatedNumber = this.namedParameterJdbcTemplate.getJdbcTemplate().update(query, values);
        return this.isInserted(updatedNumber, 1);
    }

    //we used Batch operation to minimise network trips between application and database.
    public Boolean insertedHourlyWeather(@NotNull HourlyWeather hourlyWeather) {
        String parameterizedQuery = "INSERT INTO Hourly_Weather" +
                " (city, country, description, date_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object[]> values = new ArrayList<>();

        hourlyWeather.getHourlyWeatherList().forEach(weather -> {
            Object[] valuesOfWeather = new Object[]{
                    hourlyWeather.getCity(), weather.getCountry(), weather.getDescription(),
                    weather.getDateTime(), weather.getTemp(), weather.getTempMin(),
                    weather.getTempMax(), weather.getPressure(), weather.getHumidity(),
                    weather.getWind(), weather.getFeelsLike(), weather.getClouds()};
            values.add(valuesOfWeather);
        });
        CompletableFuture<Integer> executedBatchNumber = this.executedBatchNumber(parameterizedQuery, values);
        return this.isInserted(executedBatchNumber.join(), 1);
    }

    public Boolean insertedDailyWeather(@NotNull DailyWeather dailyWeather) {
        String parameterizedQuery = "INSERT INTO Daily_Weather" +
                " (city, country, description, date_time, temp, temp_min, temp_max, pressure, humidity, wind, clouds) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        List<Object[]> values = new ArrayList<>();

        dailyWeather.getDailyWeatherList().forEach(weather -> {
            Object[] valuesOfWeather = new Object[]{
                    dailyWeather.getCity(), weather.getCountry(), weather.getDescription(),
                    weather.getDateTime(), weather.getTemp(), weather.getTempMin(),
                    weather.getTempMax(), weather.getPressure(), weather.getHumidity(),
                    weather.getWind(), weather.getClouds()};
            values.add(valuesOfWeather);
        });
        CompletableFuture<Integer> executedBatchNumber = this.executedBatchNumber(parameterizedQuery, values);
        return this.isInserted(executedBatchNumber.join(), 1);
    }
    public Boolean insertedLocation(@NotNull String cityName, @NotNull String countryName) {
        String query = "INSERT INTO locations (city, country) VALUES (:city, :country)";
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", cityName);
        locationMap.put("country", countryName);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        int updatedNumber = this.namedParameterJdbcTemplate.update(query, parameterSource);
        return this.isInserted(updatedNumber, 1);
    }

    public Boolean updatedAllWeathers(@NotNull Weather weather, @NotNull HourlyWeather hourlyWeather, @NotNull DailyWeather dailyWeather) {
        CompletableFuture<Boolean> currentWeatherUpdated = CompletableFuture.supplyAsync(() -> this.updatedCurrentWeather(weather));
        CompletableFuture<Boolean> hourlyWeatherUpdated = CompletableFuture.supplyAsync(() -> this.updatedHourlyWeather(hourlyWeather));
        CompletableFuture<Boolean> dailyWeatherUpdated = CompletableFuture.supplyAsync(() -> this.updatedDailyWeather(dailyWeather));
        List<CompletableFuture<Boolean>> areAllUpdated = List.of(currentWeatherUpdated, hourlyWeatherUpdated, dailyWeatherUpdated);
        return CompletableFuture.allOf(areAllUpdated.toArray(CompletableFuture[]::new)).thenApply(f ->{
                long updatedRows = areAllUpdated.stream().map(CompletableFuture::join).filter(isUpdated -> isUpdated.booleanValue() == Boolean.TRUE).count();
                return updatedLocation(weather.getCity()) ? updatedRows : 0L;
        })
                .join().intValue() == areAllUpdated.size();
    }
    public Boolean updatedCurrentWeather(@NotNull Weather weather) {
        String parameterizedQuery = "UPDATE Current_Weather SET " +
                "date_time = ?, description = ?, temp = ?, temp_min = ?, temp_max = ?, feels_like = ?, " +
                "pressure = ?, humidity = ?, wind = ?, clouds = ? WHERE city = ?";
        Object[] values = new Object[]{
                weather.getDateTime(),weather.getDescription(), weather.getTemp(),
                weather.getTempMin(), weather.getTempMax(), weather.getFeelsLike(),
                weather.getPressure(), weather.getHumidity(), weather.getWind(),
                weather.getClouds(), weather.getCity()};
        int updatedRows = this.namedParameterJdbcTemplate.getJdbcTemplate().update(parameterizedQuery, values);
        return this.isUpdated(updatedRows, 1);
    }
    @SuppressWarnings("all")
    public Boolean updatedHourlyWeather(@NotNull HourlyWeather hourlyWeather) {
        String deleteQuery = "DELETE FROM Hourly_Weather WHERE city = :city";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource("city", hourlyWeather.getCity());
        this.namedParameterJdbcTemplate.execute(deleteQuery, mapSqlParameterSource, PreparedStatement::executeUpdate);
            String parameterizedQuery = "INSERT INTO Hourly_Weather (date_time, description, temp, temp_min, temp_max, " +
                    "feels_like, pressure, humidity, wind, clouds, city) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            List<Object[]> values = new ArrayList<>();
            hourlyWeather.getHourlyWeatherList().forEach(weather -> {
                Object[] valuesOfWeather = new Object[]{
                        weather.getDateTime(), weather.getDescription(),
                        weather.getTemp(), weather.getTempMin(), weather.getTempMax(),
                        weather.getFeelsLike(), weather.getPressure(), weather.getHumidity(),
                        weather.getWind(), weather.getClouds(), hourlyWeather.getCity()};
                values.add(valuesOfWeather);
            });
            CompletableFuture<Integer> executedNumber = this.executedBatchNumber(parameterizedQuery, values);
            return this.isUpdated(executedNumber.join(), 1);
    }
    @SuppressWarnings("all")
    public Boolean updatedDailyWeather(@NotNull DailyWeather dailyWeather) {
        String deleteQuery = "DELETE FROM Daily_Weather WHERE city = :city";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource("city", dailyWeather.getCity());
        this.namedParameterJdbcTemplate.execute(deleteQuery, mapSqlParameterSource, PreparedStatement::executeUpdate);
            String parameterizedQuery = "INSERT INTO Daily_Weather " +
                    "(date_time, description, temp, temp_min, temp_max, pressure, humidity, wind, clouds, city) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?)";
            List<Object[]> values = new ArrayList<>();
            dailyWeather.getDailyWeatherList().forEach(weather -> {
                Object[] valuesOfWeather = new Object[]{
                        weather.getDateTime(), weather.getDescription(),
                        weather.getTemp(), weather.getTempMin(), weather.getTempMax(),
                        weather.getPressure(), weather.getHumidity(),
                        weather.getWind(), weather.getClouds(), dailyWeather.getCity()};
                values.add(valuesOfWeather);
            });
            CompletableFuture<Integer> executedNumber = this.executedBatchNumber(parameterizedQuery, values);
            return this.isUpdated(executedNumber.join(), 1);
    }
    public Boolean updatedLocation(@NotNull String city) {
        String query = "UPDATE locations SET updated_time = NOW() WHERE city = :city";
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", city);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        int updatedNumber = this.namedParameterJdbcTemplate.update(query, parameterSource);
        return this.isUpdated(updatedNumber, 1);
    }

    @Async
    CompletableFuture<Integer> executedBatchNumber(@NotNull String parameterizedQuery, @NotNull List<Object[]> parameterValues) {
        return CompletableFuture.supplyAsync(() -> this.namedParameterJdbcTemplate.getJdbcTemplate().update(parameterizedQuery, ps -> {
            for (Object[] objects : parameterValues) {
                new ArgumentPreparedStatementSetter(objects).setValues(ps);
                ps.addBatch();
            }
            ps.executeBatch();
        }));
    }

    //All methods below check if queries are executed successfully and inserts SQL scripts into the file (.sql) of corresponding table.
    //We insert SQL scripts into the file because when application is rerun, all the modification in tables could take place in other users' app (for development and test purposes only).
    private Boolean isInserted(int successfullyExecutedNumber, int requiredExecutionNumber) {
        return successfullyExecutedNumber == requiredExecutionNumber;
    }
    private Boolean isUpdated(int successfullyExecutedNumber, int requiredExecutionNumber) {
        return successfullyExecutedNumber >= requiredExecutionNumber;
    }
}