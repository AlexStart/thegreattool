package com.sam.jcc.cloud.utils.project;

import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 14-Jan-17
 */
public class GradleDependencyManagerTest {

    GradleDependencyManager manager = new GradleDependencyManager();

    @Test
    public void getsDependencies() {
        final List<Dependency> dependencies = manager.extractDependencies(
                "testCompile group: 'com.junit', name: 'junit', version: \"4.11\""
        );

        assertThat(dependencies).hasSize(1);
        final Dependency d = dependencies.get(0);

        assertThat(d.getVersion()).isEqualTo("4.11");
        assertThat(d.getArtifactId()).isEqualTo("junit");
        assertThat(d.getGroupId()).isEqualTo("com.junit");
        assertThat(d.getScope()).isEqualTo("testCompile");
    }

    @Test
    public void getsDependenciesInShortStyle() {
        final List<Dependency> dependencies = manager.extractDependencies(
                "compile('org.springframework.boot:spring-boot-starter-web:1.2.3\")"
        );

        assertThat(dependencies).hasSize(1);
        final Dependency d = dependencies.get(0);

        assertThat(d.getVersion()).isEqualTo("1.2.3");
        assertThat(d.getScope()).isEqualTo("compile");
        assertThat(d.getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(d.getArtifactId()).isEqualTo("spring-boot-starter-web");
    }
}