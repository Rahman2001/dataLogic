package datalogic.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Arrays;

@Slf4j
@Configuration
public class FlywayH2Config {
    private final DataSource h2Datasource;

    @Autowired
    public FlywayH2Config(@Qualifier("cacheDbDatasource") DataSource h2Datasource) { // we get a bean called "cacheDbDataSource" and inject it into method argument.
        this.h2Datasource = h2Datasource;                                            // "cacheDbDatasource" bean is specified in FlywayConfig.java class.
    }

    @PostConstruct
    public void migrate() {
        Flyway flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(this.h2Datasource)
                .outOfOrder(false)
                .locations("classpath:/db/migration")
                .load();

        log.info("Flyway configured and migrated successfully: " + flyway.migrate().success);
        log.info("Flyway successfully migrated tables below: ");
        Arrays.stream(flyway.info().applied()).forEach(m->
                log.info(m.getVersion().getVersion() + m.getDescription() + m.getChecksum()));
    }
}
