package datalogic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "retrofit")
public class RetrofitProperties {

    private List<EndpointProperty> endpoints;
}
