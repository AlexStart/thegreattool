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

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
public class MetadataResolver {

    private static final String DATE_TYPE = "date";
    private static final String NULL_TYPE = "object";
    private static final String STRING_TYPE = "string";
    private static final String NUMBER_TYPE = "number";

    private static final String JACKSON_DATE_FORMAT = "yyyy-MM-dd";

    private static final Map<Class, String> METADATA;

    //TODO: temp solution
    private static final Map<String, Map<String, String>> TRANSLATIONS;

    private final ObjectMapper mapper;

    static {
        METADATA = ImmutableMap.<Class, String>builder().
                put(UUID.class, STRING_TYPE).
                put(String.class, STRING_TYPE).
                put(Long.class, NUMBER_TYPE).
                put(Double.class, NUMBER_TYPE).
                put(Integer.class, NUMBER_TYPE).
                build();

        TRANSLATIONS = ImmutableMap.<String, Map<String, String>>builder().
                put("com.sam.jcc.cloud.gallery.App", singletonMap("ru", "Test App")).
                put("com.sam.jcc.cloud.gallery.App.id", singletonMap("ru", "ID")).
                put("com.sam.jcc.cloud.gallery.App.name", singletonMap("ru", "Name")).
                build();
    }

    public MetadataResolver() {
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Map<String, Object> resolve(Object data) {
        final Map<String, Object> metadata = resolve(data, getPath(data));
        final Entry<String, Object> clazz = entry(data);
        metadata.put(clazz.getKey(), clazz.getValue());
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

        if (isPrimitive(e)) {
            if (isDate(e)) {
                return entry(e, DATE_TYPE, path);
            } else {
                return entry(e, getType(e), path);
            }
        }
        return entry(e, resolve(e.getValue(), getPath(e, path)), path);
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

    private Map<String, Object> transform(List<?> data) {
        return IntStream.range(0, data.size())
                .boxed()
                .collect(
                        toMap(
                                Object::toString,
                                data::get
                        ));
    }

    private String getType(Entry<String, Object> entry) {
        return METADATA.get(entry.getValue().getClass());
    }

    private String getPath(Object data) {
        return data.getClass().getCanonicalName();
    }

    //TODO: temp solution
    private Entry<String, Object> entry(Entry<String, Object> last, Object type, String path) {
        final Map<String, String> translations = TRANSLATIONS.getOrDefault(
                getPath(last, path),
                singletonMap("en", "ABSENT_TRANSLATION")
        );
        final String translation = translations.getOrDefault(getLocale().getLanguage(), "UNKNOWN_LOCALE");

        return new SimpleEntry<>(
                last.getKey(),
                of("type", type, "translation", translation)
        );
    }

    //TODO: temp solution
    private Entry<String, Object> entry(Object data) {
        final Map<String, String> translations = TRANSLATIONS.getOrDefault(
                getPath(data),
                singletonMap("en", "ABSENT_TRANSLATION")
        );
        final String translation = translations.getOrDefault(getLocale().getLanguage(), "UNKNOWN_LOCALE");

        return new SimpleEntry<>(
                "class",
                of("type", "object", "translation", translation)
        );
    }

    private String getPath(Entry<String, Object> field, String path) {
        return path + "." + field.getKey();
    }
}
