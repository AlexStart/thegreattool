package com.sam.jcc.cloud;

import com.sam.jcc.cloud.PropertyResolver.PropertyNotFoundException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.PrintWriter;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
public class PropertyResolverTest {

    static final String EXIST_PRODUCTION_KEY = "cloud.translations";

    PropertyResolver resolver;

    PropertiesConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        final FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
        strategy.setRefreshDelay(500L);
        configuration = new PropertiesConfiguration(getPropertyFile());
        configuration.setReloadingStrategy(strategy);
        resolver = new PropertyResolver(configuration);
    }

    @After
    public void reset() {
        configuration.clear();
    }

    @Test
    public void updates() throws Exception {
        rewriteConfig("value=initial_value");
        assertThat(resolver.getValue("value")).isEqualTo("initial_value");

        rewriteConfig("value=updated_value");
        assertThat(resolver.getValue("value")).isEqualTo("updated_value");
    }

    @Test
    public void getsProperty() throws Exception {
        assertThat(PropertyResolver.getProperty(EXIST_PRODUCTION_KEY))
                .isNotNull()
                .isNotEmpty();
    }

    @Test(expected = PropertyNotFoundException.class)
    public void failsOnUnknown() throws Exception {
        resolver.getValue("unknown_key");
    }

    @Test
    public void systemPropertiesHasHighestPriority() throws Exception {
        rewriteConfig("system_key=property_value");
        System.setProperty("system_key", "system_value");

        assertThat(resolver.getValue("system_key")).isEqualTo("system_value");
    }

    void rewriteConfig(String content) throws Exception {
        try (PrintWriter writer = new PrintWriter(getPropertyFile())) {
            writer.print(content);
        }
        sleep(1000L);
    }

    File getPropertyFile() throws Exception {
        return new ClassPathResource("/test.properties").getFile();
    }
}