package serviceTests.deserializersTests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.model.DailyWeather;
import datalogic.service.deserializers.DailyWeatherDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
public class DailyWeatherDeserializerTest {
    Logger logger = LoggerFactory.getLogger(Logger.class);

    private JsonParser jsonParser;
    private final DailyWeatherDeserializer dailyWeatherDeserializer = new DailyWeatherDeserializer(DailyWeather.class);

    @BeforeEach
    public void createJsonParser() {
        String jsonString = "{\"city\":{\"id\":298388,\"name\":\"Ulus\",\"coord\":{\"lon\":32.8469,\"lat\":39.9547}," +
                "\"country\":\"TR\",\"population\":0,\"timezone\":10800},\"cod\":\"200\",\"message\":7.8911446,\"cnt\":5," +
                "\"list\":[{\"dt\":1665478800,\"sunrise\":1665460462,\"sunset\":1665501369,\"temp\":{\"day\":18.47," +
                "\"min\":9.69,\"max\":20.3,\"night\":16.47,\"eve\":19.8,\"morn\":10.06},\"feels_like\":{\"day\":17.5," +
                "\"night\":15.3,\"eve\":18.88,\"morn\":9.03},\"pressure\":1021,\"humidity\":43,\"weather\":[{\"id\":802," +
                "\"main\":\"Clouds\",\"description\":\"scatteredclouds\",\"icon\":\"03d\"}],\"speed\":1.93,\"deg\":348," +
                "\"gust\":2.05,\"clouds\":50,\"pop\":0},{\"dt\":1665565200,\"sunrise\":1665546923,\"sunset\":1665587676," +
                "\"temp\":{\"day\":19.78,\"min\":13.2,\"max\":21.02,\"night\":16.28,\"eve\":19.28,\"morn\":13.23}," +
                "\"feels_like\":{\"day\":18.65,\"night\":15.16,\"eve\":18.18,\"morn\":12.07},\"pressure\":1018,\"humidity\":32," +
                "\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcastclouds\",\"icon\":\"04d\"}]," +
                "\"speed\":2.76,\"deg\":53,\"gust\":4.09,\"clouds\":100,\"pop\":0},{\"dt\":1665651600,\"sunrise\":1665633385," +
                "\"sunset\":1665673985,\"temp\":{\"day\":19.25,\"min\":11.96,\"max\":21.08,\"night\":16.31,\"eve\":19.54," +
                "\"morn\":12.21},\"feels_like\":{\"day\":18.25,\"night\":15.3,\"eve\":18.52,\"morn\":11.08},\"pressure\":1017," +
                "\"humidity\":39,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"skyisclear\",\"icon\":\"01d\"}]," +
                "\"speed\":2.65,\"deg\":50,\"gust\":3.78,\"clouds\":8,\"pop\":0.12},{\"dt\":1665738000,\"sunrise\":1665719847," +
                "\"sunset\":1665760293,\"temp\":{\"day\":14.98,\"min\":12.32,\"max\":15.73,\"night\":12.32,\"eve\":13.36," +
                "\"morn\":13.51},\"feels_like\":{\"day\":14.1,\"night\":11.77,\"eve\":12.68,\"morn\":12.56},\"pressure\":1014," +
                "\"humidity\":60,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"lightrain\",\"icon\":\"10d\"}]," +
                "\"speed\":3.57,\"deg\":23,\"gust\":5.2,\"clouds\":100,\"pop\":0.65,\"rain\":3.58},{\"dt\":1665824400," +
                "\"sunrise\":1665806310,\"sunset\":1665846603,\"temp\":{\"day\":13.68,\"min\":11.81,\"max\":15.18,\"night\":12.95," +
                "\"eve\":14.54,\"morn\":11.92},\"feels_like\":{\"day\":12.91,\"night\":12.21,\"eve\":13.67,\"morn\":11.44}," +
                "\"pressure\":1012,\"humidity\":69,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"lightrain\"," +
                "\"icon\":\"10d\"}],\"speed\":3.05,\"deg\":4,\"gust\":4.53,\"clouds\":99,\"pop\":1,\"rain\":6.17}]}";
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            jsonParser = objectMapper.createParser(jsonString);
            jsonParser.setCodec(objectMapper);
        }
        catch (Exception e) {
            logger.error(e, () -> "Could not create JsonParser from Json String! ");
        }
    }

    @Test
    public void dailyWeatherDeserializerTest() {
        if(jsonParser != null) {
            DailyWeather dailyWeather = null;
            try {
                dailyWeather = dailyWeatherDeserializer.deserialize(jsonParser, null);
            }
            catch (Exception e) {
                logger.error(e, () -> "Could not deserialize Json into DailyWeather Object! ");
            }
            assertNotNull(dailyWeather);
            assertNotNull(dailyWeather.getDailyWeatherList());
            assertEquals(5, dailyWeather.getDailyWeatherList().size());
            assertEquals(5, dailyWeather.getForecastedTotalDays());
        }
    }
}
