package com.sam.jcc.cloud.i;

import lombok.Setter;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
@Component
class TranslationResolver {

    private static final String DEFAULT_TRANSLATIONS_RESOURCE = "translations.yml";

    @Setter
    private String resource = DEFAULT_TRANSLATIONS_RESOURCE;

    private Map<String, Map<String, String>> names = newHashMap();
    private Map<String, Map<String, String>> descriptions = newHashMap();

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

    private void parseAndSave(String key, String value) {
        final String[] values = key.split("\\.");
        if (values.length != 3) {
            throw new IllegalArgumentException("Format must be " +
                    "[Provider name].[message type].[language name]," +
                    " but was " + key);
        }

        final String provider = values[0];
        final String type = values[1];
        final String locale = values[2];

        switch (type) {
            case "name":
                names.putIfAbsent(provider, newHashMap());
                names.get(provider).put(locale, value);
                return;
            case "description":
                descriptions.putIfAbsent(provider, newHashMap());
                descriptions.get(provider).put(locale, value);
                return;
        }
        throw new IllegalArgumentException("Unknown message type: " + type);
    }

    public Map<String, String> getNames(Class<? extends IProvider<?>> provider) {
        return names.get(provider.getSimpleName());
    }

    public Map<String, String> getDescriptions(Class<? extends IProvider<?>> provider) {
        return descriptions.get(provider.getSimpleName());
    }
}