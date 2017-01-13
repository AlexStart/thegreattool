package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.utils.project.MavenDependencyManager.Dependency;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static com.sam.jcc.cloud.utils.files.FileManager.getResource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
public class MavenDependencyManagerTest {

    MavenDependencyManager manager = new MavenDependencyManager();

    @Test
    public void adds() {
        final String config = manager.add(mavenConfiguration(), jUnitDependency());

        assertThat(config)
                .isNotNull()
                .contains("junit", "4.12", "test");
    }

    @Test
    public void getsDependencies() {
        final List<Dependency> dependencies = manager.getAllDependencies(mavenConfiguration());

        assertThat(dependencies).hasSize(1);

        final Dependency dependency = dependencies.get(0);

        assertThat(dependency.getVersion()).isEqualTo("19.0");
        assertThat(dependency.getScope()).isEqualTo("compile");
        assertThat(dependency.getArtifactId()).isEqualTo("guava");
        assertThat(dependency.getGroupId()).isEqualTo("com.google.guava");
    }

    Dependency jUnitDependency() {
        final Dependency d = new Dependency();

        d.setGroupId("junit");
        d.setArtifactId("junit");
        d.setVersion("4.12");
        d.setScope("test");
        return d;
    }

    File mavenConfiguration() {
        return getResource(MavenDependencyManagerTest.class, "/valid-pom.xml");
    }
}