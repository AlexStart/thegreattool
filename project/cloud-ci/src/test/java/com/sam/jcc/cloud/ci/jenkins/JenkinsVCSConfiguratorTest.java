package com.sam.jcc.cloud.ci.jenkins;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitFileVCSConfigurator;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitProtocolVCSConfigurator;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitlabHttpVCSConfigurator;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.JenkinsCredentialsApi;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class JenkinsVCSConfiguratorTest {

    private CIProject project;

    @Before
    public void prepareProject() {
        project = new CIProject();
        project.setName("testName");
    }

    @Test
    public void generateGitFileUrlTest() throws URISyntaxException {
        URI url = new GitFileVCSConfigurator() {
            @Override
            protected URI resolveGitURL(CIProject project) {
                return super.resolveGitURL(project);
            }
        }.resolveGitURL(project);

        assertThat(url.toString()).containsPattern("file:\\/\\/\\S+\\/" + project.getName());
    }

    @Test
    public void generateGitProtocolUrlTest() throws URISyntaxException {
        URI uri = new GitProtocolVCSConfigurator() {
            @Override
            public URI resolveGitURL(CIProject project) {
                return super.resolveGitURL(project);
            }
        }.resolveGitURL(project);

        assertThat(uri.toString()).containsPattern("git:\\/\\/\\S+:\\d+\\/" + project.getName());
    }

    @Test
    public void generateGitlabHttpUrlTest() throws URISyntaxException {
        URI uri = new GitlabHttpVCSConfigurator(new InitOnceAdminBean(), new JenkinsCredentialsApi()) {
            @Override
            public URI resolveGitURL(CIProject project) {
                return super.resolveGitURL(project);
            }
        }.resolveGitURL(project);

        assertThat(uri.toString()).containsPattern("http:\\/\\/\\S+:\\d+\\/\\S+\\/\\S+\\/" + project.getName());
    }
}
