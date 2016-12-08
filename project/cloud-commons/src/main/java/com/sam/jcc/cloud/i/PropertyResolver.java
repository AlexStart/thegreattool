package com.sam.jcc.cloud.i;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
@Component
public class PropertyResolver {

    private static PropertiesConfiguration configuration;

    static {
        tryLoadProperties();

        configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
    }

    private static void tryLoadProperties() {
        try {
            configuration = new PropertiesConfiguration("cloud.properties");
        } catch (ConfigurationException e) {
            throw new InternalCloudException();
        }
    }

    public static String getProperty(String key) {
        final String value = (String) configuration.getProperty(key);

        return nonNull(value) ? tryResolveInjections(value) : null;
    }

    //TODO: use PropertyPlaceholderConfigurer
    private static String tryResolveInjections(String value) {
        if (value.contains("${user.home}")) {
            String home = System.getProperty("user.home");
            return value.replace("${user.home}", home);
        }
        return value;
    }

    @VisibleForTesting static void setReloadingStrategy(ReloadingStrategy strategy){
        configuration.setReloadingStrategy(strategy);
    }
}
