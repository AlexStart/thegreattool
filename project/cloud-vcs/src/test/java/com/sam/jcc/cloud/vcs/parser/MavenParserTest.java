package com.sam.jcc.cloud.vcs.parser;

import com.google.common.io.Resources;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.entry;

/**
 * @author Alexey Zhytnik
 * @since 28-Nov-16
 */
public class MavenParserTest {

    MavenParser parser = new MavenParser();

    @Test
    public void parses() throws Exception {
        final String pom = readPom();

        assertThat(parser.parse(pom))
                .isEqualTo(entry("com.experimental", "app"));
    }

    String readPom() throws Exception {
        return Resources.toString(
                getResource("valid-pom.xml"),
                Charset.forName("UTF-8")
        );
    }
}