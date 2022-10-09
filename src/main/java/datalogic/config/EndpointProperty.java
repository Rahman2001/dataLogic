package datalogic.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndpointProperty {
    private String serviceName;
    private String baseUrl;
}