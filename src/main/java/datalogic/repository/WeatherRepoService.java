package datalogic.repository;

import datalogic.model.DailyWeather;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import datalogic.service.serviceImpl.ApiServiceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WeatherRepoService {
    private final WeatherRepo weatherRepo;
    private final ApiServiceUtil apiServiceUtil;

    @Autowired
    public WeatherRepoService(WeatherRepo weatherRepo, ApiServiceUtil apiServiceUtil) {
        this.weatherRepo = weatherRepo;
        this.apiServiceUtil = apiServiceUtil;
    }
    //below methods are used currently, if you want more detailed database operations, feel free to build using WeatherRepo.

    public Weather updateOrInsertCurrentWeather(@NotNull String city) {
        return this.weatherRepo.exists(city) ? this.updatedAll(city, Weather.class)
                : this.insertedAll(city, Weather.class);
    }
    public Weather updateOrInsertCurrentWeather(@NotNull Double lat, @NotNull Double lon, @NotNull String city) {
        return this.weatherRepo.exists(city) ? this.updatedAll(lat, lon, city, Weather.class)
                : this.insertedAll(city, Weather.class);
    }
    public HourlyWeather updateOrInsertHourlyWeather(@NotNull String city) {
        return this.weatherRepo.exists(city) ? this.updatedAll(city, HourlyWeather.class)
                : this.insertedAll(city, HourlyWeather.class);
    }
    public HourlyWeather updateOrInsertHourlyWeather(@NotNull Double lat, @NotNull Double lon, @NotNull String city) {
        return this.weatherRepo.exists(city) ? this.updatedAll(lat, lon, city, HourlyWeather.class)
                : this.insertedAll(lat, lon, city, HourlyWeather.class);
    }
    public DailyWeather updateOrInsertDailyWeather(@NotNull String city) {
        return this.weatherRepo.exists(city) ? this.updatedAll(city, DailyWeather.class)
                : this.insertedAll(city, DailyWeather.class);
    }
    public DailyWeather updateOrInsertDailyWeather(@NotNull Double lat, @NotNull Double lon, @NotNull String city) {
        return this.weatherRepo.exists(city) ? this.updatedAll(lat, lon, city, DailyWeather.class)
                : this.insertedAll(lat, lon, city, DailyWeather.class);
    }
    private Boolean updatedOrInsertLocation(@NotNull String city, @NotNull String country) {
        return this.weatherRepo.exists(city) ? this.weatherRepo.updatedLocation(city)
                : this.weatherRepo.insertedLocation(city, country);
    }

    public Weather selectCurrentWeather(@NotNull String city) {
        return this.weatherRepo.selectCurrentWeather(city);
    }
    public HourlyWeather selectHourlyWeather(@NotNull String city) {
        return this.weatherRepo.selectHourlyWeather(city);
    }
    public DailyWeather selectDailyWeather(@NotNull String city) {
        return this.weatherRepo.selectDailyWeather(city);
    }
    private Map<String, ? extends Weather> loadWeathersFromExternalProvider(String city) {
        Map<String, ? extends Weather> weathers = this.apiServiceUtil.callAll(city);
        Weather currentWeather = weathers.get(Weather.class.getName());
        currentWeather.setCity(city);

        HourlyWeather hourlyWeather = (HourlyWeather) weathers.get(HourlyWeather.class.getName());
        List<Weather> list = hourlyWeather.getHourlyWeatherList();
        list.forEach(w-> w.setCity(city));
        hourlyWeather.setHourlyWeatherList(list);

        DailyWeather dailyWeather = (DailyWeather) weathers.get(DailyWeather.class.getName());
        list = dailyWeather.getDailyWeatherList();
        list.forEach(w-> w.setCity(city));
        dailyWeather.setDailyWeatherList(list);
        return weathers;
    }
    private Map<String, ? extends Weather> loadWeathersFromExternalProvider(Double lat, Double lon, String city) {
        Map<String, ? extends Weather> weathers = this.apiServiceUtil.callAll(lat, lon);
        Weather currentWeather = weathers.get(Weather.class.getName());
        currentWeather.setCity(city);

        HourlyWeather hourlyWeather = (HourlyWeather) weathers.get(HourlyWeather.class.getName());
        List<Weather> list = hourlyWeather.getHourlyWeatherList();
        list.forEach(w-> w.setCity(city));
        hourlyWeather.setHourlyWeatherList(list);

        DailyWeather dailyWeather = (DailyWeather) weathers.get(DailyWeather.class.getName());
        list = dailyWeather.getDailyWeatherList();
        list.forEach(w-> w.setCity(city));
        dailyWeather.setDailyWeatherList(list);
        return weathers;
    }

    @Nullable
    @SuppressWarnings("cast")
    private <T extends Weather> T updatedAll(String city, Class<T> weatherTypeForReturn) {
        Map<String, ? extends Weather> map = this.loadWeathersFromExternalProvider(city);
        Weather currentWeather = map.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) map.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) map.get(DailyWeather.class.getName());

        return this.weatherRepo.updatedAllWeathers(currentWeather, hourlyWeather, dailyWeather)
                && this.updatedOrInsertLocation(city, currentWeather.getCountry()) ?
                (T) map.get(weatherTypeForReturn.getName()) : null;
    }
    @Nullable
    @SuppressWarnings("cast")
    private <T extends Weather> T updatedAll(Double lat, Double lon, String city, Class<T> weatherTypeForReturn) {
        Map<String, ? extends Weather> map = this.loadWeathersFromExternalProvider(lat, lon, city);
        Weather currentWeather = map.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) map.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) map.get(DailyWeather.class.getName());

        return this.weatherRepo.updatedAllWeathers(currentWeather, hourlyWeather, dailyWeather)
                && this.updatedOrInsertLocation(city, currentWeather.getCountry()) ?
                (T) map.get(weatherTypeForReturn.getName()) : null;
    }

    @Nullable
    @SuppressWarnings("cast")
    private <T extends Weather> T insertedAll(String city, Class<T> weatherTypeForReturn) {
        Map<String, ? extends Weather> map = this.loadWeathersFromExternalProvider(city);
        Weather currentWeather = map.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) map.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) map.get(DailyWeather.class.getName());

        return this.weatherRepo.insertedAllWeather(currentWeather, hourlyWeather, dailyWeather)
                && this.updatedOrInsertLocation(city, currentWeather.getCountry())?
                (T) map.get(weatherTypeForReturn.getName()) : null;
    }
    @Nullable
    @SuppressWarnings("cast")
    private <T extends Weather> T insertedAll(Double lat, Double lon, String city, Class<T> weatherTypeForReturn) {
        Map<String, ? extends Weather> map = this.loadWeathersFromExternalProvider(lat, lon, city);
        Weather currentWeather = map.get(Weather.class.getName());
        HourlyWeather hourlyWeather = (HourlyWeather) map.get(HourlyWeather.class.getName());
        DailyWeather dailyWeather = (DailyWeather) map.get(DailyWeather.class.getName());

        return this.weatherRepo.insertedAllWeather(currentWeather, hourlyWeather, dailyWeather)
                && this.updatedOrInsertLocation(city, currentWeather.getCountry())?
                (T) map.get(weatherTypeForReturn.getName()) : null;
    }
}
