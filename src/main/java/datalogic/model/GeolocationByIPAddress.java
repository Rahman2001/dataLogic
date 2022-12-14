package datalogic.model;

import lombok.Data;

@Data
public class GeolocationByIPAddress {
    private String ip;
    private Double latitude;
    private Double longitude;
    private String city;
    private String country_name;
}