package datalogic.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
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
    @JsonProperty("cnt")
    private Integer forecastedTotalDays;
    @JsonProperty("list")
    private List<Weather> dailyWeatherList;
}
