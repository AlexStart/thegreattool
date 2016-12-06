package com.sam.jcc.cloud.utils.parsers;

import org.junit.Test;

import java.io.File;

import static com.sam.jcc.cloud.utils.parsers.TestResourceReader.read;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;

/**
 * @author Alexey Zhytnik
 * @since 29-Nov-16
 */
public class ProjectParserTest {

    ProjectParser parser = new ProjectParser();

    @Test
    public void parsesGradleZipProjects() throws Exception {
        final File project = read("/gradle-project.zip");

        assertThat(parser.parse(project))
                .isEqualTo(entry("com.example", "app"));
    }

    @Test
    public void parsesMavenZipProjects() throws Exception {
        final File project = read("/maven-project.zip");

        assertThat(parser.parse(project))
                .isEqualTo(entry("com.experimental", "app"));
    }
}