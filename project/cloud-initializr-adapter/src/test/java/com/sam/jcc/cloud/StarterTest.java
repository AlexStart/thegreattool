package com.sam.jcc.cloud;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

/**
 * @author Alexey Zhytnik
 * @since 09.11.2016
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class StarterTest {

    @Autowired
    private ProjectGenerator projectGenerator;

    @Autowired
    private InitializrMetadataProvider metadataProvider;

    @Test
    public void java8IsMandatoryGradle() {
        ProjectRequest request = createProjectRequest("data-jpa");
        request.setBootVersion("2.0.0.M3");
        request.setJavaVersion("1.7");
        generateGradleBuild(request);
    }

    private String generateGradleBuild(ProjectRequest request) {
        request.setType("gradle-build");
        String content = new String(projectGenerator.generateGradleBuild(request));
        if (content.isEmpty()) throw new RuntimeException();
        return content;
    }

    private ProjectRequest createProjectRequest(String... styles) {
        ProjectRequest request = new ProjectRequest();
        request.initialize(metadataProvider.get());
        request.getStyle().addAll(Arrays.asList(styles));
        return request;
    }
}
