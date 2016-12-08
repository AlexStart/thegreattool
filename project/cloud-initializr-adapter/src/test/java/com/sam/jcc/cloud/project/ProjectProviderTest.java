package com.sam.jcc.cloud.project;

import static com.sam.jcc.cloud.project.ProjectMetadataHelper.emptyProject;
import static com.sam.jcc.cloud.project.ProjectMetadataHelper.gradleProject;
import static com.sam.jcc.cloud.project.ProjectMetadataHelper.mavenProject;
import static com.sam.jcc.cloud.project.ProjectStatus.POST_PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.PRE_PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.PROCESSED;
import static com.sam.jcc.cloud.project.ProjectStatus.UNPROCESSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sam.jcc.cloud.i.BusinessCloudException;
import com.sam.jcc.cloud.i.project.IProjectMetadata;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Ignore // fix  Issue #4 
public class ProjectProviderTest {

    @Autowired
    ProjectProvider provider;

    @Test
    public void getsName() {
        final ProjectMetadata m = mavenProject();
        assertThat(provider.getName(m))
                .isEqualTo(m.getGroupId() + ":" + m.getArtifactId());
    }

    @Test
    public void getsI18NName() {
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

    @Test(expected = BusinessCloudException.class)
    public void failsWithUnknownProjectType() {
        assertThat(provider.create(emptyProject())).isNotNull();
    }

    @Test
    public void process() {
        final ProjectMetadata metadata = asProjectMetadata(provider.process(mavenProject()));
        assertThat(metadata.getStatus()).isEqualTo(PROCESSED);
        provider.postprocess(metadata);
    }

    ProjectMetadata asProjectMetadata(IProjectMetadata metadata) {
        return (ProjectMetadata) metadata;
    }
}
