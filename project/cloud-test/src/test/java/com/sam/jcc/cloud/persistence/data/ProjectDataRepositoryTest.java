package com.sam.jcc.cloud.persistence.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    public void creates() {
        repository.save(project);
        repository.delete(project);
    }

    @Test
    public void searches() {
        repository.save(project);

        final Optional<ProjectData> stored = repository.findByName(project.getName());

        assertThat(stored.isPresent()).isTrue();
        assertThat(stored.get().getName()).isEqualTo("project");

        repository.delete(project);
    }

    @Test
    public void loadSources() {
        repository.save(project);

        final ProjectData stored = repository.findOne(project.getId());
        assertThat(stored.getSources()).isEqualTo(project.getSources());

        repository.delete(project);
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
