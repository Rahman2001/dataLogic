package datalogic.service.deserializers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import datalogic.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class CurrentWeatherDeserializer extends StdDeserializer<Weather> {
    private List<String> jsonPropertyValues;
    public CurrentWeatherDeserializer(Class<? extends Weather> vc) {
        super(vc);
    }
    public CurrentWeatherDeserializer() {
        super(Weather.class);
    }

    @Override
    public Weather deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Class<Weather> cl = Weather.class;
        Field[] fields = cl.getDeclaredFields();
        this.jsonPropertyValues = Arrays.stream(fields).filter(f -> f.getDeclaredAnnotation(JsonProperty.class) != null)
                .map(f -> f.getDeclaredAnnotation(JsonProperty.class).value()).toList();

        Map<String, Object> jsonPropertyAnnotationValue = getJsonPropertyValuesAndTypes(fields);
        Map<String, String> fieldAndJsonPropertyMap = getClassFieldNamesAndJsonPropertyValues(fields);
        setValuesFromResponse(node, jsonPropertyAnnotationValue);
        replaceFirstMapKeysWithSecondMapKeys(jsonPropertyAnnotationValue, fieldAndJsonPropertyMap);
        return getWeather(jsonPropertyAnnotationValue);
    }

    protected void setValuesFromResponse(JsonNode node, @NotNull Map<String, Object> keysToFindInJSONResponse) {
        Set<String> keys = keysToFindInJSONResponse.keySet();
        keys.forEach(k -> {
            String value = node.findValue(k) != null ? node.findValue(k).asText() : null;
            if (value != null) {
                if (!value.isEmpty()) {
                    keysToFindInJSONResponse.replace(k, keysToFindInJSONResponse.get(k).equals(String.class.getSimpleName()) ?
                            value : Double.valueOf(value).intValue());
                }else {
                    if(k.equalsIgnoreCase("wind")) {
                        value = node.path(k).findValue("speed").asText();
                    }else if (k.equalsIgnoreCase("clouds")){ //if it is "clouds"
                        value = node.path(k).findValue("all").asText();
                    } else if (k.equalsIgnoreCase("temp")) {
                        int tempMin = Double.valueOf(node.path(k).findValue("min").asText()).intValue();
                        int tempMax = Double.valueOf(node.path(k).findValue("max").asText()).intValue();
                        value = String.valueOf((tempMax - tempMin)/2 + tempMin);
                    } else if (k.equalsIgnoreCase("temp_min") || k.equalsIgnoreCase("temp_max")) {
                        String[] paths = k.split("_");
                        value = String.valueOf(node.findValue(paths[0]).path(paths[1]));
                    } else if(k.equalsIgnoreCase("feels_like")){
                        value = node.path(k).findValue("day").asText();
                    }else if (k.equalsIgnoreCase("city")) {
                        value = "N/A";
                    }
                    keysToFindInJSONResponse.replace(k, keysToFindInJSONResponse.get(k).equals(String.class.getSimpleName()) ?
                            value : Double.valueOf(value).intValue());
                }
            }
            else {
                keysToFindInJSONResponse.replace(k, keysToFindInJSONResponse.get(k).equals(String.class.getSimpleName()) ?
                        "N/A" : 0);
            }
        });
    }

    protected Map<String, Object> getJsonPropertyValuesAndTypes(@NotNull Field[] fields){
        List<String> fieldTypes = Arrays.stream(fields).filter(f-> f.getDeclaredAnnotation(JsonProperty.class) != null)
                .map(f -> f.getType().getSimpleName()).toList();
        Map<String, Object> jsonPropertyValueMap = new HashMap<>();

        for(int i = 0; i < jsonPropertyValues.size(); i++) {
            jsonPropertyValueMap.put(jsonPropertyValues.get(i), fieldTypes.get(i));
        }
        return jsonPropertyValueMap;
    }
    protected Map<String, String> getClassFieldNamesAndJsonPropertyValues(@NotNull Field[] fields) {
        List<String> fieldNames = Arrays.stream(fields).filter(f-> f.getDeclaredAnnotation(JsonProperty.class) != null)
                .map(Field::getName).toList();
        Map<String, String> fieldAndJsonPropertyMap = new HashMap<>();

        for(int i = 0; i < fieldNames.size(); i++) {
            fieldAndJsonPropertyMap.put(fieldNames.get(i), jsonPropertyValues.get(i));
        }
        return fieldAndJsonPropertyMap;
    }
    protected void replaceFirstMapKeysWithSecondMapKeys(@NotNull Map<String, Object> firstMap, @NotNull Map<String, String> secondMap) {
        Set<String> keysOfSecond = secondMap.keySet();
        keysOfSecond.forEach(key -> {
            if(!firstMap.containsKey(key)) {
                Object obj = firstMap.get(secondMap.get(key));
                firstMap.put(key, obj);
                firstMap.remove(secondMap.get(key));
            }
        });
    }
    private Weather getWeather(@NotNull Map<String, Object> fieldMap) {
        return Weather.builder()
                .api_name("current_weather")
                .city((String) fieldMap.get("city"))
                .country((String) fieldMap.get("country"))
                .clouds((Integer) fieldMap.get("clouds"))
                .dateTime((String) fieldMap.get("dateTime"))
                .description((String) fieldMap.get("description"))
                .feelsLike((Integer) fieldMap.get("feelsLike"))
                .humidity((Integer) fieldMap.get("humidity"))
                .pressure((Integer) fieldMap.get("pressure"))
                .temp((Integer) fieldMap.get("temp"))
                .tempMax((Integer) fieldMap.get("tempMax"))
                .tempMin((Integer) fieldMap.get("tempMin"))
                .wind((Integer) fieldMap.get("wind")).build();
    }
}
