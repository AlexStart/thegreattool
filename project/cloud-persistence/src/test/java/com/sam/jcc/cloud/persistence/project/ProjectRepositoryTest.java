package com.sam.jcc.cloud.persistence.project;

import com.sam.jcc.cloud.persistence.DatabaseConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.sam.jcc.cloud.persistence.project.ProjectMetadataProvider.mavenTemplate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DatabaseConfiguration.class)
public class ProjectRepositoryTest {

    static final Long EXISTING_PROJECT = 5L;

    ProjectMetadataEntity project;

    @Autowired
    ProjectRepository repository;

    @Before
    public void setUp() {
        project = mavenTemplate();
        project.setId(EXISTING_PROJECT);

        repository.save(project);
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void saves() {
        repository.save(project);
    }

    @Test
    public void reads() {
        assertThat(repository.findOne(EXISTING_PROJECT))
                .extracting("id")
                .contains(EXISTING_PROJECT);
    }

    @Test
    public void updates() {
        project.setProjectName("new name");
        repository.save(project);
    }

    @Test
    public void removes() {
        repository.delete(EXISTING_PROJECT);
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void findsAll() {
        assertThat(repository.findAll()).hasSize(1);
    }
}
