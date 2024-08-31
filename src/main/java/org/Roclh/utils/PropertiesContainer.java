package org.Roclh.utils;

import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Singleton
public class PropertiesContainer {

    public static final String MANAGERS_KEY = "managers";
    public static final String BOT_TOKEN_KEY = "botToken";
    public static final String DEBUG_KEY = "debug";

    private final Map<String, String> properties = new HashMap<>();


    public String getProperty(String key) {
        return properties.get(key);
    }

    public List<String> getProperties(String key) {
        return Optional.ofNullable(properties.get(key))
                .map(property -> Arrays.stream(property.split(";")).collect(Collectors.toList()))
                .orElse(new ArrayList<>()) ;
    }

    public void addProperty(String key, String value){
        List<String> properties = getProperties(key);
        properties.add(value);
        setProperty(key, properties);
    }

    public boolean delProperty(String key, String value){
        List<String> properties = getProperties(key);
        if(properties.stream().anyMatch(val -> val.equals(value))){
            setProperty(key, properties.stream().filter(val -> !val.equals(value)).collect(Collectors.joining(";")));
            return true;
        }
        return false;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public void setProperty(String key, List<String> value) {
        properties.put(key, String.join(";", value));
    }

    public void setProperty(String key, boolean value) {
        properties.put(key, String.valueOf(value));
    }

    public boolean getBoolProperty(String key) {
        return Boolean.parseBoolean(properties.get(key));
    }
}
