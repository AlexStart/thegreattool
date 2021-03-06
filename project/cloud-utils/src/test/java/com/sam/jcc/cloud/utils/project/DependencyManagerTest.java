package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.sam.jcc.cloud.utils.files.FileManager.getResource;
import static com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 13.01.2017
 */
public class DependencyManagerTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    DependencyManager manager;

    @Before
    public void setUp() {
        manager = new DependencyManager();
        manager.setFiles(new FileManager());
        manager.setParser(new ProjectParser());
    }

    @Test
    public void addsInMaven() {
        final String config = manager.add(mavenConfiguration(), jUnitDependency());

        assertThat(config)
                .isNotNull()
                .contains("junit", "4.12", "test");
    }

    @Test
    public void addsInGradle() {
        final String config = manager.add(gradleConfiguration(), guavaDependency());

        assertThat(config)
                .isNotNull()
                .contains("com.google.guava", "19.0", "test");
    }

    @Test
    public void getsMavenDependencies() {
        final List<DependencyManager.Dependency> dependencies = manager.getAllDependencies(mavenConfiguration());

        assertThat(dependencies).hasSize(1);

        final Dependency dependency = dependencies.get(0);

        assertThat(dependency.getVersion()).isEqualTo("19.0");
        assertThat(dependency.getArtifactId()).isEqualTo("guava");
        assertThat(dependency.getGroupId()).isEqualTo("com.google.guava");
    }

    @Test
    public void getsGradleDependencies() {
        final List<Dependency> dependencies = manager.getAllDependencies(gradleConfiguration());

        assertThat(dependencies).hasSize(1);

        final Dependency dependency = dependencies.get(0);

        assertThat(dependency.getVersion()).isEqualTo("4.11");
        assertThat(dependency.getArtifactId()).isEqualTo("junit");
        assertThat(dependency.getGroupId()).isEqualTo("junit");
    }

    Dependency guavaDependency() {
        final Dependency d = new Dependency();

        d.setGroupId("com.google.guava");
        d.setArtifactId("guava");
        d.setVersion("19.0");
        d.setScope("test");
        return d;
    }

    Dependency jUnitDependency() {
        final Dependency d = new Dependency();

        d.setGroupId("junit");
        d.setArtifactId("junit");
        d.setVersion("4.12");
        d.setScope("test");
        return d;
    }

    File gradleConfiguration() {
        return extract("/gradle-project.zip");
    }

    File mavenConfiguration() {
        return extract("/maven-project.zip");
    }

    File extract(String name) {
        try {
            final File sources = tmp.newFolder();
            new ZipArchiveManager().unzip(getResource(getClass(), name), sources);
            return sources;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
