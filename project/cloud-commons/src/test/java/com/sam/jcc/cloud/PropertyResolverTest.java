package com.sam.jcc.cloud;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.junit.Before;
import org.junit.Test;

import com.sam.jcc.cloud.PropertyResolver;

import java.io.File;
import java.io.PrintWriter;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
public class PropertyResolverTest {

    @Before
    public void setUpStrategy(){
        final FileChangedReloadingStrategy s = new FileChangedReloadingStrategy();
        s.setRefreshDelay(100);
        PropertyResolver.setReloadingStrategy(s);
    }

    @Test
    public void test() throws Exception {
        rewriteConfig("value=initial_value");
        assertThat(getProperty("value")).isEqualTo("initial_value");

        rewriteConfig("value=updated_value");
        assertThat(getProperty("value")).isEqualTo("updated_value");
    }

    void rewriteConfig(String content) throws Exception {
        try (PrintWriter writer = new PrintWriter(getPropertyFile())) {
            writer.print(content);
        }
        sleep(200);
    }

    File getPropertyFile() throws Exception {
        return new File(getClass().getResource("/cloud.properties").toURI());
    }
}