package serviceTests.deserializersTests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.model.Weather;
import datalogic.service.deserializers.CurrentWeatherDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
public class CurrentWeatherDeserializerTest {
    Logger logger = LoggerFactory.getLogger(Logger.class);
    private JsonParser jsonParser;
    private final CurrentWeatherDeserializer deserializer = new CurrentWeatherDeserializer(Weather.class);

    @BeforeEach
    public void createJsonParser() {
        ObjectMapper objM = new ObjectMapper();
        String jsonString = "{\"coord\":{\"lon\":32.8469,\"lat\":39.9547},\"weather\":[{\"id\":800,\"main\":\"Clear\"," +
                "\"description\":\"clearsky\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":20.35," +
                "\"feels_like\":19.51,\"temp_min\":19.73,\"temp_max\":20.9,\"pressure\":1020,\"humidity\":41}," +
                "\"visibility\":10000,\"wind\":{\"speed\":2.57,\"deg\":0},\"clouds\":{\"all\":0},\"dt\":1665491636," +
                "\"sys\":{\"type\":2,\"id\":267643,\"country\":\"TR\",\"sunrise\":1665460462,\"sunset\":1665501369}," +
                "\"timezone\":10800,\"id\":298388,\"name\":\"Ulus\",\"cod\":200}";
        try {
            jsonParser = objM.createParser(jsonString);
            jsonParser.setCodec(objM);
        }
        catch (Exception e) {
            logger.error(e, () -> "Could not create JsonParser from Json String! ");
        }
    }

    @Test
    public void currentWeatherDeserializerTest() {
        if(jsonParser != null) {
            Weather weather = null;
            try {
                weather = deserializer.deserialize(jsonParser, null);
            }
            catch (Exception e) {
                logger.error(e, () -> "Could not deserialize successfully! ");
            }
            assertNotNull(weather);
            assertEquals("TR", weather.getCountry());
        }
    }
}
