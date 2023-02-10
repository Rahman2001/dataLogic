package datalogic.repository;

import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import datalogic.repository.rowMappers.DailyWeatherRowMapper;
import datalogic.repository.rowMappers.HourlyWeatherRowMapper;
import datalogic.repository.rowMappers.WeatherRowMapper;
import datalogic.service.FlywayMigrationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static java.sql.Statement.SUCCESS_NO_INFO;

@Repository
public class WeatherRepo {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FlywayMigrationService flywayMigrationService;

    @Autowired
    public WeatherRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                       FlywayMigrationService flywayMigrationService){
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.flywayMigrationService = flywayMigrationService;
    }
    //this method can return empty list of weathers because sub-methods can return null value.
    @Nullable
    public List<? extends Weather> selectAllWeathers(@NotNull String city) {
        CompletableFuture<Weather> currentWeather = CompletableFuture.supplyAsync(() -> this.selectCurrentWeather(city));
        CompletableFuture<HourlyWeather> hourlyWeather = CompletableFuture.supplyAsync(() -> this.selectHourlyWeather(city));
        CompletableFuture<DailyWeather> dailyWeather = CompletableFuture.supplyAsync(()-> this.selectDailyWeather(city));
        List<CompletableFuture<? extends Weather>> futureList = List.of(currentWeather, hourlyWeather, dailyWeather);
        return CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new)).thenApply(
                f -> futureList.stream().map(CompletableFuture::join).filter(Objects::nonNull).toList()).join();
    }
    @Nullable
    public Weather selectCurrentWeather(@NotNull String city) {
        String queryForCurrentWeather = "CALL selectCurrentWeatherIfUpdate(?)";
        Optional<List<Weather>> weather = Optional.of(this.namedParameterJdbcTemplate.getJdbcTemplate().query(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(queryForCurrentWeather);
            new ArgumentPreparedStatementSetter(new Object[]{city}).setValues(preparedStatement);
            return preparedStatement;
        }, new WeatherRowMapper()));
        return weather.map(weathers -> weathers.get(0)).orElse(null);
    }
    @Nullable
    public HourlyWeather selectHourlyWeather(@NotNull String city) {
        String queryForHourlyWeather = "CALL selectHourlyWeatherIfUpdate(?)";
        Optional<List<HourlyWeather>> hourlyWeather = Optional.of(this.namedParameterJdbcTemplate.getJdbcTemplate().query(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(queryForHourlyWeather);
            new ArgumentPreparedStatementSetter(new Object[]{city}).setValues(preparedStatement);
            return preparedStatement;
        }, new HourlyWeatherRowMapper()));
        return hourlyWeather.map(weathers-> weathers.get(0)).orElse(null);
    }
    public DailyWeather selectDailyWeather(@NotNull String city) {
        String queryForDailyWeather = "CALL selectDailyWeatherIfUpdate(?)";
        Optional<List<DailyWeather>> dailyWeather = Optional.of(this.namedParameterJdbcTemplate.getJdbcTemplate().query(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(queryForDailyWeather);
            new ArgumentPreparedStatementSetter(new Object[]{city, city}).setValues(preparedStatement);
            return preparedStatement;
        }, new DailyWeatherRowMapper()));
        return dailyWeather.map(dailyWeathers -> dailyWeathers.get(0)).orElse(null);
    }
    public Boolean exists(@NotNull String city) {
        String query = "EXISTS (SELECT city FROM locations WHERE city = :city)";
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", city);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        return this.namedParameterJdbcTemplate.update(query, parameterSource) > 0;
    }
    public Boolean insertedAllWeather(@NotNull Weather weather, @NotNull HourlyWeather hourlyWeather, @NotNull DailyWeather dailyWeather) {
        CompletableFuture<Boolean> currentWeatherInserted = CompletableFuture.supplyAsync(() -> this.insertedCurrentWeather(weather));
        CompletableFuture<Boolean> hourlyWeatherInserted = CompletableFuture.supplyAsync(() -> this.insertedHourlyWeather(hourlyWeather));
        CompletableFuture<Boolean> dailyWeatherInserted = CompletableFuture.supplyAsync(() -> this.insertedDailyWeather(dailyWeather));
        List<CompletableFuture<Boolean>> areAllInserted = List.of(currentWeatherInserted, hourlyWeatherInserted, dailyWeatherInserted);
        return CompletableFuture.allOf(areAllInserted.toArray(CompletableFuture[]::new)).thenApply(f ->
                areAllInserted.stream().map(CompletableFuture::join).filter(isInserted ->
                        isInserted.booleanValue() == Boolean.TRUE).count())
                .join().intValue() == areAllInserted.size();
    }
    public Boolean insertedCurrentWeather(@NotNull Weather currentWeather) {
        String query = "INSERT INTO current_weather " +
                "(date_time, description, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds, city, country) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] values = new Object[] {
                currentWeather.getDateTime(), currentWeather.getDescription(), currentWeather.getTemp(),
                currentWeather.getTempMin(), currentWeather.getTempMax(), currentWeather.getPressure(),
                currentWeather.getHumidity(), currentWeather.getWind(), currentWeather.getFeelsLike(),
                currentWeather.getClouds(), currentWeather.getCity(), currentWeather.getCountry()};

        int updatedNumber = this.namedParameterJdbcTemplate.getJdbcTemplate().update(query, ps -> {
            for(int i = 0; i < values.length; i++) {
                if(values[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) values[i]);
                }
                else {
                    ps.setString(i + 1, (String) values[i]);
                }
            }
            ps.executeUpdate();
        });
        return this.isInserted(query, updatedNumber, 1);
    }

    //we used Batch operation to minimise network trips between application and database.
    public Boolean insertedHourlyWeather(@NotNull HourlyWeather hourlyWeather) {
        String query = "INSERT INTO hourly_weather" +
                " (city, country, description, date_and_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
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
        AtomicInteger executedBatchNumber = this.executedBatchNumber(query, values);
        return this.isInserted(query, executedBatchNumber.get(), values.size());
    }

    public Boolean insertedDailyWeather(@NotNull DailyWeather dailyWeather) {
        String query = "INSERT INTO daily_weather" +
                " (city, country, description, date_and_time, temp, temp_min, temp_max, pressure, humidity, wind, feels_like, clouds) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object[]> values = new ArrayList<>();

        dailyWeather.getDailyWeatherList().forEach(weather -> {
            Object[] valuesOfWeather = new Object[]{
                    dailyWeather.getCity(), weather.getCountry(), weather.getDescription(),
                    weather.getDateTime(), weather.getTemp(), weather.getTempMin(),
                    weather.getTempMax(), weather.getPressure(), weather.getHumidity(),
                    weather.getWind(), weather.getFeelsLike(), weather.getClouds()};
            values.add(valuesOfWeather);
        });
        AtomicInteger executedBatchNumber = this.executedBatchNumber(query, values);
        return this.isInserted(query, executedBatchNumber.get(), values.size());
    }
    public Boolean insertedLocation(@NotNull String cityName, @NotNull String countryName) {
        String query = "INSERT INTO locations (city, country) VALUES (:city, :country)";
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", cityName);
        locationMap.put("country", countryName);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        int updatedNumber = this.namedParameterJdbcTemplate.update(query, parameterSource);
        return this.isInserted(query, updatedNumber, 1);
    }

    public Boolean updatedAllWeathers(@NotNull Weather weather, @NotNull HourlyWeather hourlyWeather, @NotNull DailyWeather dailyWeather) {
        CompletableFuture<Boolean> currentWeatherUpdated = CompletableFuture.supplyAsync(() -> this.updatedCurrentWeather(weather));
        CompletableFuture<Boolean> hourlyWeatherUpdated = CompletableFuture.supplyAsync(() -> this.updatedHourlyWeather(hourlyWeather));
        CompletableFuture<Boolean> dailyWeatherUpdated = CompletableFuture.supplyAsync(() -> this.updatedDailyWeather(dailyWeather));
        List<CompletableFuture<Boolean>> areAllUpdated = List.of(currentWeatherUpdated, hourlyWeatherUpdated, dailyWeatherUpdated);
        return CompletableFuture.allOf(areAllUpdated.toArray(CompletableFuture[]::new)).thenApply(f ->
                areAllUpdated.stream().map(CompletableFuture::join).filter(isUpdated -> isUpdated.booleanValue() == Boolean.TRUE).count())
                .join().intValue() == areAllUpdated.size();
    }
    public Boolean updatedCurrentWeather(@NotNull Weather weather) {
        String query = "UPDATE current_weather SET " +
                "date_time = ?, description = ?, temp = ?, temp_min = ?, temp_max = ?, feels_like = ?, " +
                "pressure = ?, humidity = ?, wind = ?, clouds = ? WHERE city = ?";
        Object[] values = new Object[]{
                weather.getDateTime(),weather.getDescription(), weather.getTemp(),
                weather.getTempMin(), weather.getTempMax(), weather.getFeelsLike(),
                weather.getPressure(), weather.getHumidity(), weather.getWind(),
                weather.getClouds(), weather.getCity()};
        int updatedRows = this.namedParameterJdbcTemplate.getJdbcTemplate().update(query, ps -> {

            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    ps.setInt(i+1, (Integer) values[i]);
                }else {
                    ps.setString(i+1, (String) values[i]);
                }
            }
            ps.executeUpdate();
        });
        return this.isUpdated(query, updatedRows, 1);
    }
    public Boolean updatedHourlyWeather(@NotNull HourlyWeather hourlyWeather) {
        String query = "UPDATE hourly_weather SET " +
                "date_time = ?, description = ?, temp = ?, temp_min = ?, temp_max = ?, feels_like = ?, " +
                "pressure = ?, humidity = ?, wind = ?, clouds = ? WHERE city = ?";
        List<Object[]> values = new ArrayList<>();
        hourlyWeather.getHourlyWeatherList().forEach(weather -> {
            Object[] valuesOfWeather = new Object[]{
                    weather.getDateTime(), weather.getDescription(),
                    weather.getTemp(), weather.getTempMin(), weather.getTempMax(),
                    weather.getFeelsLike(), weather.getPressure(), weather.getHumidity(),
                    weather.getWind(),  weather.getClouds(), hourlyWeather.getCity()};
            values.add(valuesOfWeather);
        });
        AtomicInteger executedNumber = this.executedBatchNumber(query,values);
        return this.isUpdated(query, executedNumber.get(), values.size());
    }
    public Boolean updatedDailyWeather(@NotNull DailyWeather dailyWeather) {
        String query = "UPDATE daily_weather SET " +
                "date_time = ?, description = ?, temp = ?, temp_min = ?, temp_max = ?, feels_like = ?, " +
                "pressure = ?, humidity = ?, wind = ?, clouds = ? WHERE city = ?";
        List<Object[]> values = new ArrayList<>();
        dailyWeather.getDailyWeatherList().forEach(weather -> {
            Object[] valuesOfWeather = new Object[]{
                    weather.getDateTime(), weather.getDescription(),
                    weather.getTemp(), weather.getTempMin(), weather.getTempMax(),
                    weather.getFeelsLike(), weather.getPressure(), weather.getHumidity(),
                    weather.getWind(),  weather.getClouds(), dailyWeather.getCity()};
            values.add(valuesOfWeather);
        });
        AtomicInteger executedNumber = this.executedBatchNumber(query,values);
        return this.isUpdated(query, executedNumber.get(), values.size());
    }
    public Boolean updatedLocation(@NotNull String city) {
        String query = "UPDATE locations SET updated_time = NOW() WHERE city = :city";
        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("city", city);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(locationMap);
        int updatedNumber = this.namedParameterJdbcTemplate.update(query, parameterSource);
        return this.isUpdated(query, updatedNumber, 1);
    }

    private AtomicInteger executedBatchNumber(@NotNull String parameterizedQuery, @NotNull List<Object[]> parameterValues) {
        AtomicInteger successfullyExecutedNumber = new AtomicInteger(0);
        this.namedParameterJdbcTemplate.getJdbcTemplate().update(parameterizedQuery, ps -> {
            for (Object[] objects : parameterValues) {
                for(int i = 0; i < objects.length; i++) {
                    if(objects[i] instanceof Integer) {
                        ps.setInt(i + 1, (Integer) objects[i]);
                    }else {
                        ps.setString(i + 1, (String) objects[i]);
                    }
                }
                ps.addBatch();
            }
            successfullyExecutedNumber.set(Long.valueOf(Arrays.stream(ps.executeBatch()).filter(
                    update -> update >= 0 || update == SUCCESS_NO_INFO).count()).intValue());
        });
        return successfullyExecutedNumber;
    }

    //All methods below check if queries are executed successfully and inserts SQL scripts into the file (.sql) of corresponding table.
    //We insert SQL scripts into the file because when application is rerun, all the modification in tables could take place in other users' app (for development and test purposes only).
    private Boolean isInserted(String query, int successfullyExecutedNumber, int requiredExecutionNumber) {
        boolean[] isUpdatedAndModified = new boolean[2];
        isUpdatedAndModified[0] = successfullyExecutedNumber == requiredExecutionNumber;
        isUpdatedAndModified[1] = this.flywayMigrationService.insertedInto(query);
        return isUpdatedAndModified[0] && isUpdatedAndModified[1];
    }
    private Boolean isUpdated(String query, int successfullyExecutedNumber, int requiredExecutionNumber) {
        boolean[] isUpdatedAndModified = new boolean[2];
        isUpdatedAndModified[0] = successfullyExecutedNumber == requiredExecutionNumber;
        isUpdatedAndModified[1] = this.flywayMigrationService.updatedTable(query);
        return isUpdatedAndModified[0] == isUpdatedAndModified[1];
    }
}
