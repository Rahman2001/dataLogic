package datalogic.repository.rowMappers;

import datalogic.model.DailyWeather;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DailyWeatherRowMapper implements RowMapper<DailyWeather> {
    private final WeatherRowMapper weatherRowMapper = new WeatherRowMapper();

    @Override
    public DailyWeather mapRow(ResultSet rs, int rowNum) {
        List<Weather> weatherList = new ArrayList<>();
        DailyWeather dailyWeather = null;
        try {
            while(rs.next()) {
                Weather weather = weatherRowMapper.mapRow(rs, rs.getRow());
                weatherList.add(weather);
            }
            dailyWeather = DailyWeather.builder()
                    .forecastedTotalDays(weatherList.size())
                    .dailyWeatherList(weatherList)
                    .build();
        }
        catch (SQLException e) {
            log.error("Something went wrong with mapping rows into the HourlyWeather! ", e);
        }
        return dailyWeather;
    }
}
