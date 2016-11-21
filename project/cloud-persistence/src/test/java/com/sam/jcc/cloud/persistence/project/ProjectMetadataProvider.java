package com.sam.jcc.cloud.persistence.project;

import static java.util.Collections.singletonList;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
class ProjectMetadataProvider {

    private ProjectMetadataProvider() {
    }

    public static ProjectMetadataEntity mavenTemplate() {
        final ProjectMetadataEntity project = new ProjectMetadataEntity();

        project.setJavaVersion("1.8");
        project.setArtifactId("demo");
        project.setGroupId("com.example");
        project.setProjectType("maven");

        project.setBasePackage("com.example");
        project.setDependencies(singletonList(simpleDependency()));

        project.setId(777L);
        project.setProjectName("demo_app");
        project.setVersion("0.0.1-SNAPSHOT");
        project.setWebAppPackaging(false);
        project.setDescription("maven basic project template");
        return project;
    }

    private static Dependency simpleDependency() {
        final Dependency dependency = new Dependency();
        dependency.setId(87L);
        dependency.setName("aop");
        return dependency;
    }
}
