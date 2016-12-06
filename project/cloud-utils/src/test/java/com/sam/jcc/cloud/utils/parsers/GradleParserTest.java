package com.sam.jcc.cloud.utils.parsers;

import com.google.common.io.Resources;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.entry;

/**
 * @author Alexey Zhytnik
 * @since 29-Nov-16
 */
public class GradleParserTest {

    GradleParser parser = new GradleParser();

    @Test
    public void parses() throws Exception {
        final String build = readGradleBuild();

        assertThat(parser.parse(build))
                .isEqualTo(entry("com.example", "app"));
    }

    @Test
    public void parsesGroup() {
        assertThat(parser.parseGroup("some config")).isEqualTo("");
        assertThat(parser.parseGroup("group'com.example'")).isEqualTo("com.example");
        assertThat(parser.parseGroup("group = 'com.example'")).isEqualTo("com.example");
        assertThat(parser.parseGroup("config.... \n group \t 'com.example' \n config...")).isEqualTo("com.example");
    }

    @Test
    public void parsesArtifact() {
        assertThat(parser.parseArtifact("artifact'app'")).isEqualTo("app");
        assertThat(parser.parseArtifact("artifact = \t 'app'")).isEqualTo("app");
        assertThat(parser.parseArtifact("config.... \n baseName = \t 'app' \n config...")).isEqualTo("app");
    }

    @Test(expected = RuntimeException.class)
    public void failsOnNotFound() {
        parser.parseArtifact("some config");
    }

    String readGradleBuild() throws Exception {
        return Resources.toString(
                getResource("valid-build.gradle"),
                Charset.forName("UTF-8")
        );
    }
}
