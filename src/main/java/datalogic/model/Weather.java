package datalogic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import datalogic.service.deserializers.CurrentWeatherDeserializer;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonDeserialize(using = CurrentWeatherDeserializer.class)
public class Weather {
    @JsonProperty("dt")
    private String dateTime;
    @JsonProperty("description")
    private String description;
    @JsonProperty("temp")
    private Integer temp;
    @JsonProperty("temp_min")
    private Integer tempMin;
    @JsonProperty("temp_max")
    private Integer tempMax;
    @JsonProperty("feels_like")
    private Integer feelsLike;
    @JsonProperty("pressure")
    private Integer pressure;
    @JsonProperty("humidity")
    private Integer humidity;
    @JsonProperty("wind")
    private Integer wind;
    @JsonProperty("clouds")
    private Integer clouds;
    @JsonProperty("city")
    private String city;
    @JsonProperty("country")
    private String country;
}
