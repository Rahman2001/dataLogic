package datalogic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Weather {
    private String description;
    private Integer temp;
    private Integer temp_min;
    private Integer temp_max;
    private Integer feels_like;
    private Integer pressure;
    private Integer humidity;
    private Integer wind;
    private Integer clouds;
    private String city;
    private String country;
}
