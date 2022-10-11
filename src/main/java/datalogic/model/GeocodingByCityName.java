package datalogic.model;

import lombok.Data;

@Data
public class GeocodingByCityName {
    private double lat;
    private double lon;
    private String city;
    private String country;
}
