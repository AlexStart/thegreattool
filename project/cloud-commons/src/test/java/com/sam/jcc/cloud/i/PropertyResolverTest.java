package com.sam.jcc.cloud.i;

import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

import static com.sam.jcc.cloud.i.PropertyResolver.getProperty;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
public class PropertyResolverTest {

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
        sleep(2000);
    }

    File getPropertyFile() throws Exception {
        return new File(getClass().getResource("/cloud.properties").toURI());
    }
}