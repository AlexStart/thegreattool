package com.sam.jcc.cloud.gallery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.sam.jcc.cloud.TranslationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.google.common.collect.ImmutableMap.of;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Component
public class MetadataResolver {

    private static final String DATE_TYPE = "date";
    private static final String NULL_TYPE = "object";
    private static final String STRING_TYPE = "string";
    private static final String NUMBER_TYPE = "number";

    private static final String JACKSON_DATE_FORMAT = "yyyy-MM-dd";

    private static final Map<Class, String> METADATA;

    @Autowired
    private TranslationResolver translations;

    private final ObjectMapper mapper;

    static {
        METADATA = ImmutableMap.<Class, String>builder().
                put(UUID.class, STRING_TYPE).
                put(String.class, STRING_TYPE).
                put(Long.class, NUMBER_TYPE).
                put(Double.class, NUMBER_TYPE).
                put(Integer.class, NUMBER_TYPE).
                build();
    }

    public MetadataResolver() {
        mapper = new ObjectMapper();
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
    }

    public Map<String, Object> resolve(Object obj) {
        final String path = obj.getClass().getCanonicalName();
        final Map<String, Object> metadata = resolve(obj, path);
        metadata.put("class", getTranslationByPath(path));
        return metadata;
    }

    private Map<String, Object> resolve(Object data, String path) {
        if (isList(data)) data = transform((List<?>) data);

        return transform(data)
                .entrySet()
                .stream()
                .map(e -> resolve(e, path))
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private Entry<String, Object> resolve(Entry<String, Object> e, String path) {
        if (!containsValue(e)) return entry(e, NULL_TYPE, path);

        if (isPrimitiveType(e)) {
            if (isDate(e)) {
                return entry(e, DATE_TYPE, path);
            } else {
                return entry(e, getTypeName(e), path);
            }
        }
        final Map<String, Object> subType = resolve(e.getValue(), getFieldPath(path, e));
        return entry(e, subType, path);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> transform(Object data) {
        return mapper.convertValue(data, Map.class);
    }

    private boolean containsValue(Entry<String, Object> entry) {
        return nonNull(entry.getValue());
    }

    private boolean isPrimitiveType(Entry<String, Object> entry) {
        final Class<?> clazz = entry.getValue().getClass();
        return isPrimitiveOrWrapper(clazz) || clazz.equals(String.class);
    }

    private boolean isList(Object obj) {
        return obj instanceof List;
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

    private Map<String, Object> transform(List<?> data) {
        return IntStream.range(0, data.size())
                .boxed()
                .collect(
                        toMap(
                                Object::toString,
                                data::get
                        ));
    }

    private String getTypeName(Entry<String, Object> entry) {
        return METADATA.get(entry.getValue().getClass());
    }

    private Entry<String, Object> entry(Entry<String, Object> last, Object type, String path) {
        final String fieldPath = getFieldPath(path, last);
        final String translation = getTranslationByPath(fieldPath);

        return new SimpleEntry<>(last.getKey(), of("type", type, "translation", translation));
    }

    private String getTranslationByPath(String path) {
        return translations.getLocalizedMetadata(path);
    }

    private String getFieldPath(String path, Entry<String, Object> field) {
        return path + "." + field.getKey();
    }
}
