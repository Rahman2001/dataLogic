package datalogic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "datalogic")
public class DataLogicApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataLogicApplication.class, args);
    }
}
