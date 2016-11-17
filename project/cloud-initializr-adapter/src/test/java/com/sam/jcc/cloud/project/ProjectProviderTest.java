package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.project.IProjectMetadata;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.sam.jcc.cloud.i.project.Status.*;
import static com.sam.jcc.cloud.project.ProjectMetadataHelper.gradleProject;
import static com.sam.jcc.cloud.project.ProjectMetadataHelper.mavenProject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProjectProviderTest {

    @Autowired
    ProjectProvider provider;

    @Test
    public void getsName() {
        assertThat(provider.getI18NName()).isNotEmpty();
    }

    @Test
    public void getsDescription() {
        assertThat(provider.getI18NDescription()).isNotEmpty();
    }

    @Test
    public void supportsMavenProject() {
        assertThat(provider.supports(mavenProject())).isTrue();
    }

    @Test
    public void supportsGradleProject() {
        assertThat(provider.supports(gradleProject())).isTrue();
    }

    @Test
    public void changesProjectStatus() {
        final ProjectMetadata project = spy(mavenProject());
        provider.create(project);

        final InOrder order = inOrder(project);
        order.verify(project).setStatus(UNPROCESSED);
        order.verify(project).setStatus(PRE_PROCESSED);
        order.verify(project).setStatus(PROCESSED);
        order.verify(project).setStatus(POST_PROCESSED);
    }

    @Test
    public void isAlwaysEnabled() {
        assertThat(provider.isEnabled()).isTrue();
    }

    @Test
    public void creates() {
        final IProjectMetadata metadata = provider.create(mavenProject());
        assertThat(metadata).isNotNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failsWithUnknownProjectType() {
        assertThat(provider.create(emptyProject())).isNotNull();
    }

    @Test
    public void process() {
        final ProjectMetadata metadata = asProjectMetadata(provider.process(mavenProject()));
        assertThat(metadata.getStatus()).isEqualTo(PROCESSED);
    }

    @Test
    @Ignore
    public void reads() {
    }

    @Test
    @Ignore
    public void updates() {
    }

    @Test
    @Ignore
    public void findsAll() {
    }

    @Test
    @Ignore
    public void deletes() {
        provider.delete(null);
    }

    ProjectMetadata emptyProject() {
        return new ProjectMetadata();
    }

    ProjectMetadata asProjectMetadata(IProjectMetadata metadata) {
        return (ProjectMetadata) metadata;
    }
}
