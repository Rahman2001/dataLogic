package datalogic.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class FlywayMigrationService {
    @Value("${spring.flyway.location}")
    private String DB_MIGRATION_DIRECTORY;
    private final Map<String, String> fileNamesOfTables;

    public FlywayMigrationService() {
        this.fileNamesOfTables = this.getFileNamesOfTables();
    }

    //modifies a file (.sql) of appropriate table by inserting "insert" sql script.
    public Boolean insertedInto(@NotNull String sqlScript) {
        String tableName = sqlScript.replaceAll("\\s*INSERT\\s+INTO\\s+", "").split("\\s+")[0];
        String fileName = this.fileNamesOfTables.get(tableName);
        return this.fileIsModified(fileName, sqlScript);
    }
    public Boolean updatedTable(@NotNull String sqlScript) {
        String tableName = sqlScript.replaceAll("\\s*UPDATE\\s+", "").split("\\s+")[0];
        String fileName = this.fileNamesOfTables.get(tableName);
        return fileIsModified(fileName, sqlScript);
    }
    //writes SQL scripts into indicated file (fileName) and if it is successful, returns true.
    private Boolean fileIsModified(String fileName, String sqlScript) {
        boolean isModified = false;
        File fileToModify = new File(this.DB_MIGRATION_DIRECTORY, fileName + ".sql");
        try(BufferedWriter bf = new BufferedWriter(new FileWriter(fileToModify))) {
            bf.newLine();
            bf.write(sqlScript);
            isModified = fileToModify.setLastModified(LocalDateTime.now().getMinute());
            log.info("Successfully modified a file for flyway migration : " + fileToModify.getName());
        } catch (IOException io) {
            log.error("Could not modify a file for flyway migration : " + fileToModify.getName());
        }
        return isModified;
    }
    private List<String> getFileNames(String tableName, String tableVersion) {
        File[] files = new File(this.DB_MIGRATION_DIRECTORY).listFiles();
        return files != null ? Arrays.stream(files).map(File::getName)
                .filter(name -> name.matches(tableVersion + "\\d+_\\d+__" + tableName)).toList() : new ArrayList<>();
    }

    //creates a map where tables' names and corresponding file names are stored.
    private Map<String, String> getFileNamesOfTables() {
        Map<String, String> nameVersionMap = new HashMap<>();
        nameVersionMap.put("locations", "V1_");
        nameVersionMap.put("current_weather", "V2_");
        nameVersionMap.put("hourly_weather", "V3_");
        nameVersionMap.put("daily_weather", "V4_");
        nameVersionMap.forEach((tableName, tableVersion) -> {
            String[] filesNames = this.getFileNames(tableName, tableVersion).toArray(String[]::new);
            Long greatestSecondDigit = this.getGreatestSecondDigitInVersion(filesNames, tableVersion);
            Long greatestThirdDigit = this.getGreatestThirdDigitInVersion(filesNames, tableVersion);
            String fileName = new StringBuilder().append(tableVersion).append(greatestSecondDigit)
                    .append("_").append(greatestThirdDigit).append("__").append(tableName).toString();
            nameVersionMap.replace(tableName, tableVersion, fileName);
        });
        return nameVersionMap;
    }

    // find the greatest value of version in a second digit (ex.: (V3_0_2, V3_1_3) -> 1)
    private Long getGreatestSecondDigitInVersion(String[] fileNames, String tableVersion) {
        fileNames = Arrays.stream(fileNames).sorted().toArray(String[]::new);
        String secondDigit = fileNames[fileNames.length-1].replaceAll(tableVersion + "_", "").split("_")[0];
        return Long.valueOf(secondDigit);
    }

    // find the greatest value of version in third digit (ex.: (V3_0_2, V3_0_1) -> 2)
    private Long getGreatestThirdDigitInVersion(String[] fileNames, String tableVersion) {
        fileNames = Arrays.stream(fileNames).sorted().toArray(String[]::new);
        String thirdDigit = fileNames[fileNames.length-1].replaceAll(tableVersion + "_\\d+_", "").split("__")[0];
        return Long.valueOf(thirdDigit);
    }
}
