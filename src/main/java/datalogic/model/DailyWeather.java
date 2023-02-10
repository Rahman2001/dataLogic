package datalogic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import datalogic.service.deserializers.DailyWeatherDeserializer;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonDeserialize(using = DailyWeatherDeserializer.class)
public class DailyWeather extends Weather{
    private String api_name = "daily_weather";
    @JsonProperty("cnt")
    private Integer forecastedTotalDays;
    @JsonProperty("list")
    private List<Weather> dailyWeatherList;
}
