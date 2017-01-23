package com.sam.jcc.cloud.persistence.data;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProjectDataRepositoryTest {

    @Autowired
    ProjectDataRepository repository;

    ProjectData project = projectTemplate();

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void creates() {
        repository.save(project);
        repository.delete(project);
    }

    @Test
    public void searches() {
        repository.save(project);

        final Optional<ProjectData> stored = repository.findByName(project.getName());

        assertThat(stored.orElseThrow(RuntimeException::new)).isEqualTo(project);
    }

    @Test
    public void updates() {
        repository.save(project);

        project.setCi(null);
        repository.save(project);

        final ProjectData stored = repository.findOne(project.getId());
        assertThat(stored.getCi()).isNull();
    }

    @Test
    public void loadSources() {
        repository.save(project);

        final ProjectData stored = repository.findOne(project.getId());
        assertThat(stored.getSources()).isEqualTo(project.getSources());
    }

    @Test(expected = JpaSystemException.class)
    public void ciShouldBeUnique() {
        final ProjectData data_1 = new ProjectData();
        data_1.setName("name_1");
        data_1.setCi("ci-project");

        final ProjectData data_2 = new ProjectData();
        data_2.setName("name_2");
        data_2.setCi("ci-project");

        repository.save(data_1);
        repository.save(data_2);
    }

    ProjectData projectTemplate() {
        final ProjectData p = new ProjectData();

        p.setVcs("vcs-project");
        p.setName("project");
        p.setCi("ci-project");
        p.setDataSupport(false);

        final byte[] sources = new byte[10_000];
        new Random().nextBytes(sources);
        p.setSources(sources);
        return p;
    }
}
