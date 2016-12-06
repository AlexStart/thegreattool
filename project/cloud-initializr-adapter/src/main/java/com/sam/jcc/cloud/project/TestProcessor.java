package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.InternalCloudException;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.squareup.javapoet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;

/**
 * @author Alexey Zhytnik
 * @since 21.11.2016
 */
@Component
class TestProcessor {

    @Autowired
    private FileManager fileManager;

    public void process(ProjectMetadata metadata) {
        TypeSpec clazz = build();
        JavaFile javaFile = JavaFile
                .builder(metadata.getBasePackage(), clazz)
                .build();

        File testFolder = getTestDir(metadata);
        fileManager.cleanDir(testFolder);

        writeToFile(javaFile, testFolder);
    }

    private TypeSpec build() {
        return TypeSpec.classBuilder("DemoApplicationTests")
                .addAnnotation(runWith())
                .addAnnotation(springBootTest())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(contextLoadTest())
                .build();
    }

    private AnnotationSpec springBootTest() {
        final ClassName clazz = ClassName.get(
                "org.springframework.boot.test.context",
                "SpringBootTest"
        );
        return AnnotationSpec.builder(clazz).build();
    }

    private AnnotationSpec runWith() {
        final ClassName runWith = ClassName.get("org.junit.runner", "RunWith");
        return AnnotationSpec.builder(runWith)
                .addMember("value", "$T.class", springRunner())
                .build();
    }

    private ClassName springRunner() {
        return ClassName.get(
                "org.springframework.test.context.junit4",
                "SpringRunner"
        );
    }

    private MethodSpec contextLoadTest() {
        return MethodSpec.methodBuilder("contextLoads")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(test())
                .returns(void.class)
                .build();
    }

    private AnnotationSpec test() {
        final ClassName clazz = ClassName.get("org.junit", "Test");
        return AnnotationSpec.builder(clazz).build();
    }

    private File getTestDir(ProjectMetadata metadata) {
        return new File(metadata.getDirectory(), "src/test/java/");
    }

    private void writeToFile(JavaFile javaFile, File folder) {
        try {
            javaFile.writeTo(folder);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }
}
