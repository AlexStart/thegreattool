package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import org.junit.Test;

import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.loadProject;
import static com.trilead.ssh2.util.IOUtils.closeQuietly;
import static java.lang.Thread.sleep;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Zhytnik
 * @since 23-Dec-16
 */
public class JenkinsProviderTest extends JenkinsBaseTest {

    CIProject project;
    JenkinsProvider provider;

    public void setUp() throws Exception {
        provider = new JenkinsProvider(emptyList());
        provider.setJenkins(jenkins);

        project = loadProject("maven", temp.newFolder());
    }

    @Test
    public void supportsCIProjects() {
        assertThat(provider.supports(project)).isTrue();
    }

    @Test
    public void checksAccessToServer() {
        assertThat(provider.isEnabled()).isTrue();

        Jenkins jenkins = spy(this.jenkins);
        when(jenkins.isEnabled()).thenReturn(false);
        provider.setJenkins(jenkins);

        assertThat(provider.isEnabled()).isFalse();
    }

    @Test
    public void creates() {
        provider.create(project);
        provider.delete(project);
    }

    @Test
    public void reads() throws Exception {
        provider.create(project);
        waitWhileProcessing(project);

        final CIProject build = (CIProject) provider.read(project);
        assertThat(build.getBuild()).isNotNull();

        closeQuietly(build.getBuild());
        provider.delete(project);
    }

    @Test
    public void updates() throws Exception {
        provider.create(project);
        sleep(250L);
        provider.update(project);
        waitWhileProcessing(project);

        final CIProject build = (CIProject) provider.read(project);
        assertThat(build.getBuild()).isNotNull();

        closeQuietly(build.getBuild());
        provider.delete(project);
    }

    @Test
    public void findsAll() throws Exception {
        assertThat(provider.findAll()).isNotNull().isEmpty();

        provider.create(project);

        final String expected = project.getArtifactId();

        assertThat(provider.findAll())
                .hasSize(1).element(0)
                .extracting("artifactId").containsOnly(expected);

        provider.delete(project);
    }
}
