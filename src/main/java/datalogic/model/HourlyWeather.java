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
    private Integer forecasted_total_hours;
    private LocalDateTime hours_between;
    private List<Weather> hourly_weather;

    public void addHourlyWeather(Weather weather) {
        this.hourly_weather.add(weather);
    }
}
