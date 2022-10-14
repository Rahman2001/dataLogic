package datalogic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HourlyWeather extends Weather{
    private Integer hours_to_forecast;
    private LocalDateTime date_and_time_of_update;
    private List<Weather> hourly_weather;

    public void addHourlyWeather(Weather weather) {
        this.hourly_weather.add(weather);
    }
}
