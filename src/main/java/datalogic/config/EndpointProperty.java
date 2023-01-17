package datalogic.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointProperty {
    private String serviceName;
    private String baseUrl;
    private String path;
    private String apiKey;

}