package datalogic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import datalogic.service.deserializers.HourlyWeatherDeserializer;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonDeserialize(using = HourlyWeatherDeserializer.class)
public class HourlyWeather extends Weather{
    private String api_name;
    @JsonProperty("cnt")
    private Integer forecastedTotalHours;
    @JsonProperty("list")
    private List<Weather> hourlyWeatherList;
}
