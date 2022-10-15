package datalogic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWeather {
    private Integer forecasted_total_days;
    private LocalDateTime dates_between;
    private List<Weather> daily_weather;

    public void addDailyWeather(Weather weather) {
        this.daily_weather.add(weather);
    }
}
