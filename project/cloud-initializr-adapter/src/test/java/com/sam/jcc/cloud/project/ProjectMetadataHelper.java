package com.sam.jcc.cloud.project;

import static java.util.Collections.singletonList;

/**
 * @author Alexey Zhytnik
 * @since 15.11.2016
 */
class ProjectMetadataHelper {

    private ProjectMetadataHelper() {
    }

    public static ProjectMetadata emptyProject() {
        return new ProjectMetadata();
    }

    public static ProjectMetadata mavenProject() {
        return basicProject("maven-project");
    }

    public static ProjectMetadata gradleProject() {
        return basicProject("gradle-project");
    }

    private static ProjectMetadata basicProject(String projectType) {
        final ProjectMetadata project = new ProjectMetadata();

        project.setJavaVersion("1.8");
        project.setArtifactId("demo-" + projectType);
        project.setGroupId("com.example");
        project.setProjectType(projectType);
        project.setBootVersion("1.4.2.RELEASE");

        project.setBasePackage("com.example");
        project.setDependencies(singletonList("web"));

        project.setProjectName("demo_app");
        project.setVersion("0.0.1-SNAPSHOT");
        project.setWebAppPackaging(false);
        project.setDescription("maven basic project template");
        return project;
    }
}
