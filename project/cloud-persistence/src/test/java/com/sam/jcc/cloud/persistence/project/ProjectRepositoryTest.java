package com.sam.jcc.cloud.persistence.project;

import com.sam.jcc.cloud.persistence.DatabaseConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.sam.jcc.cloud.persistence.project.ProjectEntityHelper.gradleProject;
import static com.sam.jcc.cloud.persistence.project.ProjectEntityHelper.mavenProject;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DatabaseConfiguration.class)
public class ProjectRepositoryTest {

    ProjectMetadataEntity project;

    @Autowired
    ProjectRepository repository;

    @Before
    public void setUp() {
        project = repository.save(mavenProject());
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void saves() {
        repository.save(gradleProject());
    }

    @Test
    public void reads() {
        assertThat(repository.findOne(project.getId()))
                .extracting("projectName")
                .contains("demo_app");
    }

    @Test
    public void updates() {
        project.setProjectName("new name");
        repository.save(project);
    }

    @Test
    public void removes() {
        repository.delete(project.getId());
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void findsAll() {
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    public void findByGroupIdAndArtifactId() {
        assertThat(repository.findByGroupIdAndArtifactId("com.example", "demo-maven")).isNotNull();
    }

    @Test(expected = JpaSystemException.class)
    public void storesOnlyUniqueProjects() {
        repository.save(mavenProject());
    }
}
