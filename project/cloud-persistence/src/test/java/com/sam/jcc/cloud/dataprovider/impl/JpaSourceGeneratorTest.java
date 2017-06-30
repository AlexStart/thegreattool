package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import com.sam.jcc.cloud.utils.project.DependencyManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

import static com.sam.jcc.cloud.utils.files.FileManager.getResource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 18.01.2017
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        FileManager.class,
        UnzipSandbox.class,
        ProjectParser.class,
        ZipArchiveManager.class,
        DependencyManager.class,
        MySqlInjector.class,
        JpaSourceGenerator.class,
        TableNameValidator.class
})
public class JpaSourceGeneratorTest {

    static final String DTO = "src/main/java/com/zhytnik/app/dto/ExampleDTO.java";
    static final String SERVICE = "src/main/java/com/zhytnik/app/service/ExampleService.java";
    static final String SERVICE_IMPL = "src/main/java/com/zhytnik/app/service/impl/ExampleServiceImpl.java";
    static final String SERVICE_TEST = "src/test/java/com/zhytnik/app/service/ExampleServiceTest.java";
    static final String CONVERTER = "src/main/java/com/zhytnik/app/converter/ExampleConverter.java";
    static final String CONVERTER_IMPL = "src/main/java/com/zhytnik/app/converter/impl/ExampleConverterImpl.java";
    static final String ENTITY = "src/main/java/com/zhytnik/app/persistence/entity/Example.java";
    static final String DAO = "src/main/java/com/zhytnik/app/persistence/repository/ExampleDAO.java";
    static final String TEST = "src/test/java/com/zhytnik/app/persistence/repository/ExampleDAOTest.java";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Autowired
    FileManager files;

    @Autowired
    ZipArchiveManager manager;

    @Autowired
    JpaSourceGenerator generator;

    @Before
    public void setUp() {
        generator.setGroupId("com.zhytnik");
    }

    @Test
    public void generates() throws IOException {
        final AppData app = mavenApp();
        generator.generate(app);

        final File sources = readSources(app);

        assertThat(new File(sources, DTO)).exists();
        assertThat(new File(sources, SERVICE)).exists();
        assertThat(new File(sources, SERVICE_IMPL)).exists();
        assertThat(new File(sources, SERVICE_TEST)).exists();
        assertThat(new File(sources, CONVERTER)).exists();
        assertThat(new File(sources, CONVERTER_IMPL)).exists();
        assertThat(new File(sources, DAO)).exists();
        assertThat(new File(sources, TEST)).exists();
        assertThat(new File(sources, ENTITY)).exists();
    }

    File readSources(AppData app) throws IOException {
        final File zip = tmp.newFile(), dir = tmp.newFolder();

        files.write(app.getSources(), zip);
        manager.unzip(zip, dir);
        return dir;
    }

    AppData mavenApp() throws IOException {
        final AppData data = new AppData();

        data.setAppName("app");
        data.setSources(files.read(getResource(getClass(), "/maven-project.zip")));
        return data;
    }
}