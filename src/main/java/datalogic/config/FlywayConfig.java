package datalogic.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class FlywayConfig {

    @Value("${app.pooled-db}")
    private boolean pooledDb;

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.h2datasource")
    public DataSourceProperties datasourceProperties() {
        log.info("Pooled connection is enabled: " + pooledDb);
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.h2datasource.configuration")
    public DataSource dataSource() {
        if(pooledDb) { // if configurations in application.yaml file are used in this class, then proceed.
            return this.datasourceProperties().initializeDataSourceBuilder()
                    .type(HikariDataSource.class).build();
        }
        else { // otherwise, we configure our datasource manually.
            final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(datasourceProperties().getDriverClassName());
            driverManagerDataSource.setUsername(datasourceProperties().getUsername());
            driverManagerDataSource.setPassword(datasourceProperties().getPassword());
            driverManagerDataSource.setUrl(datasourceProperties().getUrl());
            return driverManagerDataSource;
        }
    }

    @Bean
    @ConfigurationProperties("app.datasource.cache-db")
    public DataSourceProperties cacheDbDataSourceProperties() {
        log.info("Cache Database properties are received: " + pooledDb);
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.cache-db.configuration")
    public DataSource cacheDbDatasource() {
        if(pooledDb) {
            return this.cacheDbDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        }
        else {
            final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName(cacheDbDataSourceProperties().getDriverClassName());
            driverManagerDataSource.setUsername(cacheDbDataSourceProperties().getUsername());
            driverManagerDataSource.setPassword(cacheDbDataSourceProperties().getPassword());
            driverManagerDataSource.setUrl(cacheDbDataSourceProperties().getUrl());
            return driverManagerDataSource;
        }
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() { // the reason why created this method here is that jdbcTemplate needs configured cache datasource.
        final JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(cacheDbDatasource());
        jdbcTemplate.setFetchSize(1000);
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

}
