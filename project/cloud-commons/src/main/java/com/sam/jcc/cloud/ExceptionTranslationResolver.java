package com.sam.jcc.cloud;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Maps.newHashMap;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.util.Objects.isNull;

/**
 * @author Alexey Zhytnik
 * @since 14.12.2016
 */
public class ExceptionTranslationResolver {

    private static final ExceptionTranslationResolver INSTANCE =
            new ExceptionTranslationResolver(getProperty("error.translations"));

    private Map<String, Map<String, String>> translations = newHashMap();

    ExceptionTranslationResolver(String resource) {
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
        final String key = property.substring(0, property.lastIndexOf("."));
        final String lang = property.substring(key.length() + 1);

        translations.putIfAbsent(key, newHashMap());
        translations.get(key).put(lang, value);
    }

    public static String getTranslation(String key) {
        return INSTANCE.getValue(key);
    }

    @VisibleForTesting String getValue(String key) {
        final Map<String, String> keyTranslations = translations.get(key);
        if (isNull(keyTranslations)) {
            return "";
        }
        return keyTranslations.get(getLanguage());
    }

    private String getLanguage() {
        return LocaleContextHolder.getLocale().getLanguage();
    }
}
