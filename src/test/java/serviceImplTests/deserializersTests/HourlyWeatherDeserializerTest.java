package serviceImplTests.deserializersTests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.model.HourlyWeather;
import datalogic.service.deserializers.HourlyWeatherDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class HourlyWeatherDeserializerTest {
    Logger logger = LoggerFactory.getLogger(Logger.class);
    private JsonParser jsonParser;
    private HourlyWeatherDeserializer deserializer = new HourlyWeatherDeserializer(HourlyWeather.class);

    @BeforeEach
    public void createJsonParser() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{\"cod\":\"200\",\"message\":0,\"cnt\":5,\"list\":[{\"dt\":1674925200,\"main\":{\"temp\":5.8," +
                "\"feels_like\":4.78,\"temp_min\":5.8,\"temp_max\":6.99,\"pressure\":1009,\"sea_level\":1009," +
                "\"grnd_level\":912,\"humidity\":87,\"temp_kf\":-1.19},\"weather\":[{\"id\":802,\"main\":\"Clouds\"," +
                "\"description\":\"scatteredclouds\",\"icon\":\"03n\"}],\"clouds\":{\"all\":40},\"wind\":{\"speed\":1.56," +
                "\"deg\":198,\"gust\":2.61},\"visibility\":10000,\"pop\":0.3,\"sys\":{\"pod\":\"n\"}," +
                "\"dt_txt\":\"2023-01-2817:00:00\"},{\"dt\":1674928800,\"main\":{\"temp\":5.98,\"feels_like\":4.66," +
                "\"temp_min\":5.98,\"temp_max\":6.71,\"pressure\":1009,\"sea_level\":1009,\"grnd_level\":913,\"humidity\":83," +
                "\"temp_kf\":-0.73},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"brokenclouds\"," +
                "\"icon\":\"04n\"}],\"clouds\":{\"all\":51},\"wind\":{\"speed\":1.85,\"deg\":196,\"gust\":1.95}," +
                "\"visibility\":10000,\"pop\":0.26,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-01-2818:00:00\"}," +
                "{\"dt\":1674932400,\"main\":{\"temp\":5.95,\"feels_like\":4.18,\"temp_min\":5.95,\"temp_max\":6.17," +
                "\"pressure\":1010,\"sea_level\":1010,\"grnd_level\":913,\"humidity\":80,\"temp_kf\":-0.22}," +
                "\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"brokenclouds\",\"icon\":\"04n\"}]," +
                "\"clouds\":{\"all\":64},\"wind\":{\"speed\":2.32,\"deg\":209,\"gust\":2.56},\"visibility\":10000," +
                "\"pop\":0.35,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-01-2819:00:00\"},{\"dt\":1674936000,\"main\":{\"temp\":5.61," +
                "\"feels_like\":3.29,\"temp_min\":5.49,\"temp_max\":5.61,\"pressure\":1011,\"sea_level\":1011,\"grnd_level\":913," +
                "\"humidity\":78,\"temp_kf\":0.12},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"brokenclouds\"," +
                "\"icon\":\"04n\"}],\"clouds\":{\"all\":76},\"wind\":{\"speed\":2.93,\"deg\":211,\"gust\":4.42},\"visibility\":10000," +
                "\"pop\":0.37,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-01-2820:00:00\"},{\"dt\":1674939600,\"main\":{\"temp\":5.14," +
                "\"feels_like\":2.24,\"temp_min\":4.97,\"temp_max\":5.14,\"pressure\":1012,\"sea_level\":1012,\"grnd_level\":914," +
                "\"humidity\":79,\"temp_kf\":0.17},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcastclouds\"," +
                "\"icon\":\"04n\"}],\"clouds\":{\"all\":88},\"wind\":{\"speed\":3.65,\"deg\":223,\"gust\":7.72},\"visibility\":10000," +
                "\"pop\":0.28,\"sys\":{\"pod\":\"n\"},\"dt_txt\":\"2023-01-2821:00:00\"}],\"city\":{\"id\":298388," +
                "\"name\":\"Ulus\",\"coord\":{\"lat\":39.9547,\"lon\":32.8469},\"country\":\"TR\",\"population\":0," +
                "\"timezone\":10800,\"sunrise\":1674882057,\"sunset\":1674918105}}";
        try {
            jsonParser = objectMapper.createParser(jsonString);
            jsonParser.setCodec(objectMapper);
        }
        catch (Exception e) {
            logger.error(e, () -> "Could not create JsonParser from Json String! ");
        }
    }

    @Test
    public void hourlyWeatherDeserializerTest() {
        if(jsonParser != null) {
            HourlyWeather hourlyWeather = null;
            try {
                hourlyWeather = deserializer.deserialize(jsonParser, null);
            }
            catch (Exception e) {
                logger.error(e, ()-> "Could not deserialize into HourlyWeather Object! ");
            }
            assertNotNull(hourlyWeather);
            assertNotNull(hourlyWeather.getHourlyWeatherList());
            assertEquals(5, hourlyWeather.getHourlyWeatherList().size());
            assertEquals(5, hourlyWeather.getForecastedTotalHours());
        }
    }
}
