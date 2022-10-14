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
public class DailyWeather extends Weather{
    private Integer cnt;
    private LocalDateTime date_and_time;
    private List<Weather> daily_weather;

    public void addDailyWeather(Weather weather) {
        this.daily_weather.add(weather);
    }
}
