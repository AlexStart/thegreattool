package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.project.impl.MavenProjectProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.google.common.collect.Lists.newArrayList;
import static com.sam.jcc.cloud.project.ProjectMetadataHelper.mavenProject;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 22.11.2016
 */
@Ignore("not actual, need refactoring")
@SpringBootTest
@RunWith(SpringRunner.class)
public class MavenProjectProviderCRUDTest {

    @Autowired
    MavenProjectProvider provider;

    ProjectMetadata metadata = mavenProject();

    @Before
    public void setUp() {
        provider.create(metadata);
    }

    @After
    public void tearDown() {
        provider.delete(metadata);
    }

    @Test
    public void reads() {
        final ProjectMetadata m = loadMavenProject();

        assertThat(m.getJavaVersion()).isEqualTo("1.8");
        assertThat(m.getArtifactId()).isEqualTo("demo-maven-project");
        assertThat(m.getGroupId()).isEqualTo("com.example");
        assertThat(m.getProjectType()).isEqualTo("maven-project");
        assertThat(m.getBootVersion()).isEqualTo("1.4.2.RELEASE");
        assertThat(m.getJavaVersion()).isEqualTo("1.8");
        assertThat(m.getBasePackage()).isEqualTo("com.example");
        assertThat(m.getDependencies()).contains("web");
        assertThat(m.getProjectName()).isEqualTo("demo_app");
        assertThat(m.getVersion()).isEqualTo("0.0.1-SNAPSHOT");
        assertThat(m.getWebAppPackaging()).isFalse();
        assertThat(m.getDescription()).isEqualTo("maven-project basic template");
    }

    @Test
    public void updates() {
        final ProjectMetadata m = loadMavenProject();
        m.setDependencies(newArrayList("aop", "jpa"));

        final ProjectMetadata created = (ProjectMetadata) provider.update(m);

        assertThat(created.getDependencies()).containsOnly("aop", "jpa");
        assertThat(provider.findAll()).hasSize(1);
    }

    @Test
    public void findsAll() {
        assertThat(provider.findAll()).isNotEmpty();
    }

    ProjectMetadata loadMavenProject() {
        final ProjectMetadata m = new ProjectMetadata();

        m.setGroupId("com.example");
        m.setArtifactId("demo-maven-project");
        m.setProjectType("maven-project");
        return (ProjectMetadata) provider.read(m);
    }
}
