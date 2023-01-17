package datalogic.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@EnableConfigurationProperties(RetrofitProperties.class)
public class RetrofitConfig {

    private List<EndpointProperty> endpointProperties;

    @Autowired
    public RetrofitConfig(RetrofitProperties retrofitProperties) {
        RetrofitProperties r2 = retrofitProperties;
        this.endpointProperties = r2.getEndpoints();
    }

    @Bean
    public List<EndpointProperty> restEndpoints() {
        return this.endpointProperties;
    }
}
