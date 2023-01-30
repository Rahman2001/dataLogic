package datalogic.service.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import datalogic.model.HourlyWeather;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HourlyWeatherDeserializer extends CurrentWeatherDeserializer{

    public HourlyWeatherDeserializer(Class<HourlyWeather> vc) {
        super(vc);
    }

    @Override
    public HourlyWeather deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return getHourlyWeather(node, ctxt);
    }
    private HourlyWeather getHourlyWeather(JsonNode node, DeserializationContext ctxt) {
        HourlyWeather hourlyWeather = HourlyWeather.builder().build();
        Integer cnt = Integer.valueOf(node.findValue("cnt").asText());
        List<Weather> weathers = getWeatherList(node, ctxt);
        hourlyWeather.setHourlyWeatherList(weathers);
        hourlyWeather.setForecastedTotalHours(cnt);
        return hourlyWeather;
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
