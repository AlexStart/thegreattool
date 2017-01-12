package com.sam.jcc.cloud;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IProvider;

import lombok.Setter;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
@Component
public class TranslationResolver {

    @Setter
    private String resource = PropertyResolver.getProperty("cloud.translations");

    private final Map<String, Map<String, String>> names = newHashMap();
    private final Map<String, Map<String, String>> metadata = newHashMap();
    private final Map<String, Map<String, String>> descriptions = newHashMap();

    @PostConstruct
    public void setUp() {
        final Properties translations = loadTranslations(resource);

        for (String key : translations.stringPropertyNames()) {
            parseAndSave(key, translations.getProperty(key));
        }
    }

    private Properties loadTranslations(String resource) {
        final YamlPropertiesFactoryBean props = new YamlPropertiesFactoryBean();
        props.setResources(new ClassPathResource(resource));
        return props.getObject();
    }

    private void parseAndSave(String property, String value) {
        final String[] values = property.split("\\.");
        if (values.length < 3) {
            throw new IllegalArgumentException("Format must be " +
                    "[property key].[message type].[language name]," +
                    " but was " + property);
        }

        final String key = extractKey(values, values.length - 3);
        final String type = values[values.length - 2];
        final String locale = values[values.length - 1];

        switch (type) {
            case "name":
                names.putIfAbsent(key, newHashMap());
                names.get(key).put(locale, value);
                return;
            case "description":
                descriptions.putIfAbsent(key, newHashMap());
                descriptions.get(key).put(locale, value);
                return;
            case "metadata":
                metadata.putIfAbsent(key, newHashMap());
                metadata.get(key).put(locale, value);
                return;
        }
        throw new IllegalArgumentException("Unknown message type: " + type);
    }

    private String extractKey(String[] values, int endIndex) {
        return stream(copyOfRange(values, 0, endIndex + 1)).collect(joining("."));
    }

    public Map<String, String> getNames(Class<? extends IProvider<?>> provider) {
        return names.get(provider.getSimpleName());
    }

    public Map<String, String> getDescriptions(Class<? extends IProvider<?>> provider) {
        return descriptions.get(provider.getSimpleName());
    }

    public Map<String, String> getMetadata(String key) {
        return metadata.get(key);
    }
}
