package datalogic.config;

import datalogic.repository.WeatherDataUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableScheduling
@Slf4j
public class TaskSchedulerConfig implements SchedulingConfigurer {
    private Queue<Map<String, LocalDateTime>> citiesToUpdate;
    private final WeatherDataUpdateService weatherDataUpdateService;
    @Autowired
    public TaskSchedulerConfig(final WeatherDataUpdateService weatherDataUpdateService) {
        this.weatherDataUpdateService = weatherDataUpdateService;
        this.citiesToUpdate = this.weatherDataUpdateService.getCitiesForSchedule();
    }
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(this.weatherDataUpdateService, triggerContext -> {
            Date lastSchedule = triggerContext.lastActualExecutionTime();
            return (Objects.nonNull(lastSchedule) &&
                    LocalTime.now().isBefore(LocalTime.ofInstant(lastSchedule.toInstant(), ZoneId.systemDefault()))) ?
                    lastSchedule : scheduledExecution();
        });
    }
    private Date scheduledExecution() {
        if(this.citiesToUpdate == null || this.citiesToUpdate.isEmpty()) {
            this.citiesToUpdate = this.weatherDataUpdateService.getCitiesForSchedule();
        }
        Map<String, LocalDateTime> cityTimeMap = this.citiesToUpdate.poll();
        AtomicReference<Instant> instant = new AtomicReference<>();
        AtomicReference<Date> date = new AtomicReference<>();
        if(cityTimeMap != null && !cityTimeMap.isEmpty()) {
            cityTimeMap.forEach((city, time) -> {
                LocalDateTime timeToSchedule = calculateTime(time);
                instant.set(Timestamp.valueOf(timeToSchedule.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toInstant());
                date.set(Date.from(instant.get()));
                log.info("SchedulerTaskRegistrar has registered a task for  " + timeToSchedule.toLocalTime());
            });
        }
        return date.get();
    }
    private LocalDateTime calculateTime(LocalDateTime lastUpdateDate) {
        boolean isSameDayMonthYear = lastUpdateDate.toLocalDate().isEqual(LocalDate.now());
        LocalDateTime scheduledTime;

        if (isSameDayMonthYear && LocalTime.now().getHour() == lastUpdateDate.getHour()) {
            scheduledTime = lastUpdateDate.toLocalTime().plusMinutes(5).isAfter(LocalTime.now()) ?
            lastUpdateDate.plusMinutes(5) : LocalDateTime.now();
        }   else {
            scheduledTime = LocalDateTime.now();
        }
        return scheduledTime;
    }
}
