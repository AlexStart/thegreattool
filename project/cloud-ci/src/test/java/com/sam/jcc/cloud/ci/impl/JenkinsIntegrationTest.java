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

import static com.sam.jcc.cloud.ci.CIProjectStatus.IN_PROGRESS;
import static java.lang.Thread.sleep;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public class JenkinsIntegrationTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    Jenkins jenkins;
    CIProject project;

    @Before
    public void setUp() throws Exception {
        jenkins = new Jenkins();
        jenkins.setWorkspace(temp.newFolder());

        project = new CIProject();
        project.setArtifactId("TempProject");
        project.setSources(copyProjectSourcesInto(temp.newFolder()));
    }

    @Test
    public void getsBuild() throws Exception {
        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        final InputStream build = jenkins.getBuild(project);
        assertThat(build).isNotNull();
        closeQuietly(build);

        jenkins.delete(project);
    }

    void waitWhileProcessing(CIProject p) throws Exception {
        while (jenkins.getStatus(p) == IN_PROGRESS) sleep(100L);
    }

    File copyProjectSourcesInto(File dir) throws Exception {
        final File sources = new ClassPathResource("/app.zip").getFile();
        new ZipArchiveManager().unzip(sources, dir);
        return dir;
    }
}
