package datalogic.repository.rowMappers;

import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HourlyWeatherRowMapper implements RowMapper<HourlyWeather> {
    private final WeatherRowMapper weatherRowMapper = new WeatherRowMapper();

    @Override
    public HourlyWeather mapRow(ResultSet rs, int row) {
        List<Weather> weatherList = new ArrayList<>();
        HourlyWeather hourlyWeather = null;
        try {
            while(rs.next()) {
                Weather weather = weatherRowMapper.mapRow(rs, rs.getRow());
                weatherList.add(weather);
            }
            hourlyWeather = HourlyWeather.builder()
                    .forecastedTotalHours(weatherList.size())
                    .hourlyWeatherList(weatherList)
                    .city(weatherList.get(0).getCity())
                    .build();
        }
        catch (SQLException e) {
            log.error("Something went wrong with mapping rows into the HourlyWeather either because ->\n" +
                    "-> return value is NULL or due to technical issues!", e);
        }
        return hourlyWeather;
    }

}
