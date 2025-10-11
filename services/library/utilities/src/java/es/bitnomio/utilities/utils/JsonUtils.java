package es.bitnomio.utilities.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Json utils for Bitnomio data
 * <p>
 * 2024 [Peter]
 */
public final class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class.getName());

    private static final Boolean FILTER_SENSIBLE_DATA = Boolean.TRUE;


    /**
     * This method provides an easy way to transform any json to a key value object
     * keys are always a string, value are objects so in order to work with this data
     * we better should know which kind of object are we dealing with.
     *
     * @param json string formatted from request
     * @return Mapped object with json data
     */
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(json)) {
            return map;
        }

        final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try {
            map = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("Error mapping json");
            log.error(e.getMessage());
        }

        return map;
    }

    public static <T> Optional<T> jsonToObject(String json, Class<T> object) {

        final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        if (StringUtils.isBlank(json)) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(mapper.readValue(json, object));
        } catch (JsonProcessingException e) {
            log.error("Error mapping string to object");
            log.error(e.getMessage());
        }

        return Optional.empty();
    }

    public static <T> Optional<List<T>> jsonToObjectList(String json, Class<T> object) {

        final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        final TypeFactory factory = mapper.getTypeFactory();
        final JavaType listOfT = factory.constructCollectionType(List.class, object);

        if (!StringUtils.isBlank(json)) {
            try {
                return Optional.ofNullable(mapper.readValue(json, listOfT));
            } catch (JsonProcessingException e) {
                log.error("Error mapping string to object list");
                log.error(e.getMessage());
            }
        }

        return Optional.empty();
    }

    public static Map<String, Object> objectToMap(Object obj) {

        Map<String, Object> map = new HashMap<>();

        if (Objects.isNull(obj)) {
            return map;
        }

        final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        try {
            map = mapper.convertValue(obj, new TypeReference<>() {
            });
        } catch (Exception e) {

            log.warn("Error mapping object, try if it is a List of objects");
            try {
                var data = mapper.convertValue(obj, new TypeReference<List<Map<String, Object>>>() {
                });
                map.put("data", data);
            } catch (Exception ex) {
                log.error("Error mapping object to map<String, Object>");
                log.error(ex.getMessage());
            }
        }

        return map;
    }

    public static Optional<String> objectToJson(Object obj) {
        return objectToJson(FILTER_SENSIBLE_DATA, obj);
    }

    public static Optional<String> objectToJson(Boolean filterData, Object obj) {

        final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        if (Objects.isNull(obj)) {
            return Optional.empty();
        }

        var filteredData = filterSensibleData(filterData, obj);

        try {

            String jsonString = mapper.writeValueAsString(filteredData);

            return Optional.of(jsonString);

        } catch (JsonProcessingException e) {
            log.error("Error mapping object");
            log.error(e.getMessage());
        }

        return Optional.empty();
    }


    private static Map<String, Object> filterSensibleData(Boolean filterData, Object obj) {

        final var objMap = objectToMap(obj);

        if (filterData) {
            return filterSensibleData(objMap);
        }

        return objMap;

    }

    private static Map<String, Object> filterSensibleData(Map<String, Object> objMap) {

        final Set<String> MASK_KEYS = new HashSet<>(Arrays.asList(
            "pass",
            "password",
            "token",
            "secret",
            "app_id",
            "Authorization"));

        for (String mask : MASK_KEYS) {
            if (objMap.containsKey(mask)) {
                objMap.put(mask, "<secret>");
            }
        }

        return objMap;

    }

}
