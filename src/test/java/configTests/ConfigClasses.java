package configTests;

import datalogic.config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {RetrofitConfig.class, RetrofitProperties.class, FlywayConfig.class})
@SpringBootTest
public class ConfigClasses {

    @Autowired
    RetrofitConfig retrofitConfig;

    @Autowired
    RetrofitProperties retrofitProperties;

    @Autowired
    FlywayConfig flywayConfig;

    @Test
    public void retrofitConfig(){
        assertNotNull(retrofitConfig);
    }

    @Test
    public void retrofitProperty(){
        assertNotNull(retrofitProperties);
        assertNotNull(retrofitProperties.getEndpoints().get(0));
    }

    @Test
    public void flywayConfig(){
        assertNotNull(this.flywayConfig.datasourceProperties());
        assertNotNull(this.flywayConfig.dataSource());
        assertNotNull(this.flywayConfig.cacheDbDataSourceProperties());
        assertNotNull(this.flywayConfig.cacheDbDatasource());
        assertNotNull(this.flywayConfig.namedParameterJdbcTemplate());
    }

    @Test
    public void correlationId(){
        assertNotNull(CorrelationId.create());
        assertNotNull(CorrelationId.value());
    }
}
