package datalogic.service.clientService;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class FlywayMigrationService {
    @Value("${spring.flyway.location}")
    private String DB_MIGRATION_DIRECTORY;

    //creates a table with either HourlyWeather name or DailyWeather name and after, creates .sql file in 'db.migration' directory.
    public Boolean createTableWithWeatherAndCityName(@NotNull String sqlScript) {
        String tableName = sqlScript.replaceAll("\\s*CREATE\\s+TABLE\\s+", "").split("\\s+")[0];
        String tableVersion = this.getTableVersionBYTableName(tableName);

        tableVersion = tableVersion + this.increaseSecondDigitOfVersion(tableVersion, tableName) +
                this.increaseThirdDigitOfVersion(tableVersion, tableName) + "__";

        File file = new File(this.DB_MIGRATION_DIRECTORY, tableVersion + tableName + ".sql");

        try {
            return file.createNewFile();
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
    }

    //alters or deletes a table of either HourlyWeather or DailyWeather and after, makes changes to appropriate files in 'db.migration' directory.
    public Boolean alterOrDropTable(@NotNull String sqlScript) {
        String tableName = sqlScript.replaceAll("\\s*\\S+\\s+TABLE\\s+", "").split("\\s+")[0];
        String tableVersion = this.getTableVersionBYTableName(tableName);

        tableVersion = tableVersion + this.increaseSecondDigitOfVersion(tableVersion, tableName) + "_" +
                0 + "__";

        try {
            File fileToCreate = new File(this.DB_MIGRATION_DIRECTORY, tableVersion + tableName + ".sql");
            boolean[] isCreatedAndModified = new boolean[2];
            isCreatedAndModified[0] = fileToCreate.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(fileToCreate));
            bf.write(sqlScript);
            isCreatedAndModified[1] = fileToCreate.setLastModified(LocalDateTime.now().getMinute());

            return isCreatedAndModified[0] && isCreatedAndModified[1];

        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
    }

    public Boolean insertDataIntoFile(@NotNull String sqlScript) {
        String tableName = sqlScript.replaceAll("\\s*INSERT\\s+INTO\\s+", "").split("\\s+")[0];
        String tableVersion = this.getTableVersionBYTableName(tableName);

        Long thirdDigitOfVersion = this.increaseThirdDigitOfVersion(tableVersion, tableName);
        Long secondDigitOfVersion = this.findTheGreatestInSecondDigitOfVersion(new String[]{tableName}, tableVersion);
        boolean isModified = false;

        File fileToModify = new File(this.DB_MIGRATION_DIRECTORY, tableVersion + secondDigitOfVersion + thirdDigitOfVersion + tableName + ".sql");
        try(BufferedWriter bf = new BufferedWriter(new FileWriter(fileToModify))) {
            bf.newLine();
            bf.write(sqlScript);
            isModified = fileToModify.setLastModified(LocalDateTime.now().getMinute());
        } catch (IOException io) {
            io.printStackTrace();
        }

        return isModified;
    }

    private String getTableVersionBYTableName(String tableName) {
        String tableType = tableName.replaceAll("_\\S+_\\S+", ""); //replaces parts of string which given regex. (ex.: "HourlyWeather_of_Ankara" -> "HourlyWeather")
        return tableType.equalsIgnoreCase("HourlyWeather") ? "V3_" : "V4_";
    }

    // find the greatest value of version in a second digit and increases that digit for creation of new version. (ex.: V3_0_2 -> V3_1_0)
    private Long findTheGreatestInSecondDigitOfVersion(String[] fileNames, String tableVersion) {
        fileNames = Arrays.stream(fileNames).sorted().toArray(String[]::new);
        String secondDigit = fileNames[fileNames.length-1].replaceAll(tableVersion + "_", "").split("_")[0];
        Long secondDigitInLong = Long.valueOf(secondDigit);
        secondDigitInLong++;
        return secondDigitInLong;
    }

    // find the greatest value of version in third digit and increases that digit for creation of new version. (ex.: V3_0_2 -> V3_0_3)
    private Long findTheGreatestInThirdDigitOfVersion(String[] fileNames, String tableVersion) {
        fileNames = Arrays.stream(fileNames).sorted().toArray(String[]::new);
        String thirdDigit = fileNames[fileNames.length-1].replaceAll(tableVersion + "_\\d+_", "").split("__")[0];
        Long thirdDigitInLong = Long.valueOf(thirdDigit);
        thirdDigitInLong++;
        return thirdDigitInLong;
    }

    // used when an existing table's structure or name is being modified where the second digit of version of a table has to be upgraded.
    private Long increaseSecondDigitOfVersion(String tableVersion, String tableName) {
        File files = new File(this.DB_MIGRATION_DIRECTORY);
        String[] fileNames = files.list((fileIn, fileName) -> fileName.matches(tableVersion +
                "_\\d+_\\d+__" + tableName));

        if(fileNames == null) {
            return 0L;
        }
        return findTheGreatestInSecondDigitOfVersion(fileNames, tableVersion);
    }

    // used when a table is being created where the third digit of version of a table has to be upgraded.
    private Long increaseThirdDigitOfVersion(String tableVersion, String tableName) {
        File files = new File(this.DB_MIGRATION_DIRECTORY);
        String[] fileNames = files.list((fileIn, fileName) -> fileName.matches(tableVersion +
                "_\\d+_\\d+__" + tableName));

        if(fileNames == null) {
            return 0L;
        }
        return findTheGreatestInThirdDigitOfVersion(fileNames, tableVersion);
    }
}
