package com.sam.jcc.cloud;

import com.sam.jcc.cloud.exception.InternalCloudException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
public class PropertyResolverTest {

    static final String EXIST_KEY = "cloud.translations";

    PropertyResolver resolver;

    @Before
    public void setUpStrategy() throws Exception {
        final FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
        strategy.setRefreshDelay(100L);
        final PropertiesConfiguration configuration = new PropertiesConfiguration(getPropertyFile());
        configuration.setReloadingStrategy(strategy);

        resolver = new PropertyResolver(configuration);
    }

    @Test
    public void updates() throws Exception {
        rewriteConfig("value=initial_value");
        assertThat(resolver.getValue("value")).isEqualTo("initial_value");

        rewriteConfig("value=updated_value");
        assertThat(resolver.getValue("value")).isEqualTo("updated_value");
    }

    @Test(expected = InternalCloudException.class)
    public void failsOnUnknown() throws Exception {
        rewriteConfig("");
        resolver.getValue("value");
    }

    @Test
    public void getsProperty() {
        assertThat(PropertyResolver.getProperty(EXIST_KEY))
                .isNotNull()
                .isNotEmpty();
    }

    void rewriteConfig(String content) throws Exception {
        try (PrintWriter writer = new PrintWriter(getPropertyFile())) {
            writer.print(content);
        }
        sleep(200);
    }

    File getPropertyFile() throws Exception {
        return new File(getClass().getResource("/test.properties").toURI());
    }
}