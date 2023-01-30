package datalogic.service.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.model.DailyWeather;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DailyWeatherDeserializer extends CurrentWeatherDeserializer{

    public DailyWeatherDeserializer(Class<DailyWeather> vc) {
        super(vc);
    }

    @Override
    public DailyWeather deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return getDailyWeather(node, ctxt);
    }
    private DailyWeather getDailyWeather(JsonNode node, DeserializationContext ctxt) {
        DailyWeather dailyWeather = DailyWeather.builder().build();
        Integer cnt = Integer.valueOf(node.findValue("cnt").asText());
        List<Weather> weathers = getWeatherList(node, ctxt);
        dailyWeather.setDailyWeatherList(weathers);
        dailyWeather.setForecastedTotalDays(cnt);
        return dailyWeather;
    }
    private List<Weather> getWeatherList(JsonNode node, DeserializationContext ctxt) {
        JsonNode jsonNode = node.path("list");
        List<Weather> weatherList = new ArrayList<>();
        if(jsonNode.isArray()) {
            for(JsonNode n : jsonNode) {
                try {
                    weatherList.add(super.deserialize(n.traverse(new ObjectMapper()), ctxt));
                }
                catch (Exception e) {
                    log.error("Could not deserialize Json into Weather to create a list of Weather object! - ", e);
                }
            }
        }
        return weatherList;
    }
}
