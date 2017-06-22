package com.sam.jcc.cloud.ci.jenkins;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitFileVCSConfigurator;
import com.sam.jcc.cloud.ci.jenkins.config.vcs.GitProtocolVCSConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
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
        //TODO: check after Rodion's commit
        assertThat(url.toString()).containsPattern("\\S+/" + project.getName());
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

}
