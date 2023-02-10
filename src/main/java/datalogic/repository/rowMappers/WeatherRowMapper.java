package datalogic.repository.rowMappers;

import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


@Slf4j
public class WeatherRowMapper implements RowMapper<Weather> {

    @Override
    public Weather mapRow(ResultSet rs, int rowNum) throws SQLException {
        Weather weather = null;
        try {
            if (rs.next()) {
                weather = Weather.builder()
                        .city(rs.getString("city"))
                        .country(rs.getString("country"))
                        .description(rs.getString("description"))
                        .temp(rs.getInt("temp"))
                        .tempMin(rs.getInt("temp_min"))
                        .tempMax(rs.getInt("temp_max"))
                        .dateTime(rs.getString("date_time"))
                        .wind(rs.getInt("wind"))
                        .humidity(rs.getInt("humidity"))
                        .feelsLike(rs.getInt("feels_like"))
                        .pressure(rs.getInt("pressure"))
                        .clouds(rs.getInt("clouds"))
                        .build();
            }
        }
        catch (SQLException sqlException) {
            log.error("Could not map rows from the query for Weather object either because ->\n" +
                    "-> return value is NULL or due to technical issues! ", sqlException);
        }
        return weather;
    }
}
