package datalogic.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DailyWeather extends Weather{
    private Integer forecasted_total_days;
    private LocalDateTime dates_between;
    private List<Weather> daily_weather;

    public void addDailyWeather(Weather weather) {
        this.daily_weather.add(weather);
    }
}
