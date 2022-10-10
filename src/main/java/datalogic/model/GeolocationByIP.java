package datalogic.model;

import lombok.Data;

@Data
public class GeolocationByIP {
    private String IP;
    private String CITY_NAME;
    private String COUNTRY_NAME;
    private Long LATITUDE;
    private Long LONGITUDE;
}