package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIBuildNotFoundException;
import com.sam.jcc.cloud.event.DefaultLoggingEventManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.sam.jcc.cloud.ci.CIProjectStatus.CONFIGURED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CREATED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.DELETED;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.HAS_NO_BUILD;
import static com.sam.jcc.cloud.ci.CIProjectStatus.UPDATED;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.loadProject;
import static com.sam.jcc.cloud.ci.util.CIProjectTemplates.projectWithFailedTest;
import static java.lang.Thread.sleep;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Zhytnik
 * @since 23-Dec-16
 */
public class JenkinsProviderTest extends JenkinsBaseTest {

    CIProject project;
    JenkinsProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new JenkinsProvider(singletonList(new DefaultLoggingEventManager<>()));
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

        Jenkins jenkins = spy(JenkinsBaseTest.jenkins);
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
    @Ignore
    public void reads() throws Exception {
        assertThat(project.getStatus()).isEqualTo(CONFIGURED);

        provider.create(project);
        assertThat(project.getStatus()).isEqualTo(CREATED);

        waitWhileProcessing(project);

        final CIProject build = (CIProject) provider.read(project);
        assertThat(build.getBuild()).isNotNull();
        assertThat(project.getStatus()).isEqualTo(HAS_BUILD);

        provider.delete(project);
        assertThat(project.getStatus()).isEqualTo(DELETED);
    }

    @Test
    @Ignore
    public void failsWithFailedBuild() throws Exception {
        final CIProject project = projectWithFailedTest(temp.newFolder());

        provider.create(project);
        waitWhileProcessing(project);

        try {
            provider.read(project);
            fail("should not return failed build");
        } catch (CIBuildNotFoundException expected) {
            assertThat(project.getStatus()).isEqualTo(HAS_NO_BUILD);
        } finally {
            provider.delete(project);
        }
    }

    @Test
    @Ignore
    public void updates() throws Exception {
        provider.create(project);
        sleep(250L);
        provider.update(project);
        assertThat(project.getStatus()).isEqualTo(UPDATED);

        waitWhileProcessing(project);

        final CIProject build = (CIProject) provider.read(project);
        assertThat(build.getBuild()).isNotNull();

        provider.delete(project);
    }

    @Test
    @Ignore
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
