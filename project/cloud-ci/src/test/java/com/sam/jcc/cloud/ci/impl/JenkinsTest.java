package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;

import static com.sam.jcc.cloud.ci.CIProjectStatus.*;
import static java.lang.Thread.sleep;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public class JenkinsTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    Jenkins jenkins;
    CIProject project;

    @Before
    public void setUp() throws Exception {
        jenkins = new Jenkins();
        jenkins.setWorkspace(temp.newFolder());

        project = setUpProject("/maven-project.zip");
    }

    @Test
    public void buildsApp() throws Exception {
        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        final InputStream build = jenkins.getBuild(project);
        assertThat(build).isNotNull();
        closeQuietly(build);

        assertThat(jenkins.getStatus(project)).isEqualTo(COMPLETED);

        jenkins.delete(project);
    }

    @Test
    public void knowsBuildFails() throws Exception {
        final CIProject project = setUpProject("/wrong-project.zip");

        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        assertThat(jenkins.getStatus(project)).isEqualTo(FAILED);
        jenkins.delete(project);
    }

    @Test
    public void checksUnknownProjects() {
        assertThat(jenkins.getStatus(project)).isEqualTo(UNREGISTERED);
    }

    void waitWhileProcessing(CIProject p) throws Exception {
        while (jenkins.getStatus(p) == IN_PROGRESS) sleep(100L);
    }

    CIProject setUpProject(String src) throws Exception {
        final CIProject p = new CIProject();
        p.setArtifactId("TempProject");
        p.setSources(copyProjectSourcesInto(src, temp.newFolder()));
        return p;
    }

    File copyProjectSourcesInto(String src, File dir) throws Exception {
        final File sources = new ClassPathResource(src).getFile();
        new ZipArchiveManager().unzip(sources, dir);
        return dir;
    }
}
