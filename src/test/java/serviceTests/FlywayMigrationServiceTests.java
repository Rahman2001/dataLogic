package serviceTests;

import datalogic.DataLogicApplication;
import datalogic.service.FlywayMigrationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataLogicApplication.class})
public class FlywayMigrationServiceTests {
    @Autowired
    private FlywayMigrationService flywayMigrationService;

    public void findThirdDigitVersionTest() {
        String[] fileNames = {"V3_0_1__HourlyWeather_of_Ankara.sql"};
        assertEquals(this.flywayMigrationService.findTheGreatestInThirdDigitOfVersion(fileNames, "V3"),
                1);
    }

}
