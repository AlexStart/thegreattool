package com.sam.jcc.cloud.gallery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
public class MetadataResolver {

    private static final String DATE_TYPE = "date";
    private static final String NULL_TYPE = "object";
    private static final String NUMBER_TYPE = "number";

    private static final String JACKSON_DATE_FORMAT = "yyyy-MM-dd";

    private static final Map<Class, String> METADATA;

    private final ObjectMapper mapper;

    static {
        METADATA = ImmutableMap.<Class, String>builder().
                put(UUID.class, "string").
                put(String.class, "string").
                put(Long.class, NUMBER_TYPE).
                put(Double.class, NUMBER_TYPE).
                put(Integer.class, NUMBER_TYPE).
                build();
    }

    public MetadataResolver() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Map<String, Object> resolve(Object data) {
        if (isList(data)) data = transform((List<?>) data);

        return transform(data)
                .entrySet()
                .stream()
                .map(this::resolve)
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private Entry<String, Object> resolve(Entry<String, Object> e) {
        if (!containsValue(e)) return entry(e, NULL_TYPE);

        if (isPrimitive(e)) {
            if (isDate(e)) {
                return entry(e, DATE_TYPE);
            } else {
                return entry(e, getType(e));
            }
        }
        return entry(e, resolve(e.getValue()));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> transform(Object data) {
        return mapper.convertValue(data, Map.class);
    }

    private boolean containsValue(Entry<String, Object> entry) {
        return nonNull(entry.getValue());
    }

    private boolean isPrimitive(Entry<String, Object> entry) {
        final Class<?> clazz = entry.getValue().getClass();
        return isPrimitiveOrWrapper(clazz) || clazz.equals(String.class);
    }

    private boolean isList(Object data) {
        return data instanceof List;
    }

    private boolean isDate(Entry<String, Object> entry) {
        final Object val = entry.getValue();

        if (!(val instanceof String)) return false;

        final DateFormat jacksonFormat = new SimpleDateFormat(JACKSON_DATE_FORMAT);
        try {
            jacksonFormat.parse((String) val);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<Integer, Object> transform(List<?> data) {
        return IntStream.range(0, data.size())
                .boxed()
                .collect(
                        toMap(
                                i -> i,
                                data::get
                        ));
    }

    private String getType(Entry<String, Object> entry) {
        return METADATA.get(entry.getValue().getClass());
    }

    private Entry<String, Object> entry(Entry<String, Object> last, Object value) {
        return new SimpleEntry<>(last.getKey(), value);
    }
}
