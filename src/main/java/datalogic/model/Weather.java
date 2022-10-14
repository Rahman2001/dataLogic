package datalogic.model;

import lombok.Data;

@Data
public class WeatherData {
    private String description;
    private Integer temp;
    private Integer temp_min;
    private Integer temp_max;
    private Integer feels_like;
    private Integer pressure;
    private Integer humidity;
    private Integer wind;
    private Integer clouds;
    private String country;
}
