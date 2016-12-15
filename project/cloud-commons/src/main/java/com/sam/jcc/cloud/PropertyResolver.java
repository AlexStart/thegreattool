package com.sam.jcc.cloud;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.exception.InternalCloudException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
@Component
public class PropertyResolver {

    private static PropertyResolver INSTANCE = new PropertyResolver();

    private PropertiesConfiguration configuration;

    private PropertyResolver() {
        tryLoadProperties();

        configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
    }

    private void tryLoadProperties() {
        try {
            configuration = new PropertiesConfiguration("cloud.properties");
        } catch (ConfigurationException e) {
            throw new InternalCloudException(e);
        }
    }

    @VisibleForTesting PropertyResolver(PropertiesConfiguration configuration) {
        this.configuration = configuration;
    }

    public static String getProperty(String key) {
        return INSTANCE.getValue(key);
    }

    @VisibleForTesting String getValue(String key) {
        final String value = configuration.getString(key);

        if (isNull(value)) {
            throw new PropertyNotFoundException(key);
        }
        return tryResolveInjections(value);
    }

    //TODO: use PropertyPlaceholderConfigurer
    private String tryResolveInjections(String value) {
        if (value.contains("${user.home}")) {
            String home = System.getProperty("user.home");
            return value.replace("${user.home}", home);
        }
        return value;
    }

    public static class PropertyNotFoundException extends InternalCloudException {
        public PropertyNotFoundException(String property) {
            super("property.notFound", property);
        }
    }
}
