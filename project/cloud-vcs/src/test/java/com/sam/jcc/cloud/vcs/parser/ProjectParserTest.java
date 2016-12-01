package com.sam.jcc.cloud.vcs.parser;

import org.junit.Test;

import java.io.File;
import java.net.URL;

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
        final File project = readProject("gradle-project.zip");

        assertThat(parser.parse(project))
                .isEqualTo(entry("com.example", "app"));
    }

    @Test
    public void parsesMavenZipProjects() throws Exception {
        final File project = readProject("maven-project.zip");

        assertThat(parser.parse(project))
                .isEqualTo(entry("com.experimental", "app"));
    }

    File readProject(String name) throws Exception {
        final URL resource = this.getClass().getResource("/" + name);
        return new File(resource.toURI());
    }
}