package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.ci.exception.CIProjectNotFoundException;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;

import static com.sam.jcc.cloud.PropertyResolver.setProperty;
import static com.sam.jcc.cloud.ci.CIBuildStatus.*;
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

        final InputStream build = jenkins.getLastSuccessfulBuild(project);
        assertThat(build).isNotNull();
        closeQuietly(build);

        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(SUCCESSFUL);

        jenkins.delete(project);
    }

    @Test
    //TODO: fails
    public void knowsAboutBuildFail() throws Exception {
        final CIProject project = setUpProject("/wrong-project.zip");

        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(FAILED);
        jenkins.delete(project);
    }

    @Test(expected = CIBuildNotFoundException.class)
    public void failsOnGetBuildFailedProject() throws Exception {
        final CIProject project = setUpProject("/wrong-project.zip");

        jenkins.create(project);
        jenkins.build(project);
        waitWhileProcessing(project);

        try {
            jenkins.getLastSuccessfulBuild(project);
        } finally {
            jenkins.delete(project);
        }
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnDeleteUnknown(){
        jenkins.delete(project);
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnBuildUnknown(){
        jenkins.build(project);
    }

    @Test(expected = CIProjectNotFoundException.class)
    public void failsOnGetBuildUnknown(){
        jenkins.getLastSuccessfulBuild(project);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void runsInSelectedDirectory() throws Exception {
        final File workspace = temp.newFolder();
        setProperty("ci.workspace.folder", workspace.getAbsolutePath());

        final Jenkins jenkins = new Jenkins();
        assertThat(jenkins.getWorkspace().getRoot()).isEqualTo(workspace);
    }

    @Test
    public void checksUnknownProjects() {
        assertThat(jenkins.getLastBuildStatus(project)).isEqualTo(UNKNOWN);
    }

    void waitWhileProcessing(CIProject p) throws Exception {
        while (jenkins.getLastBuildStatus(p) == IN_PROGRESS) sleep(100L);
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
