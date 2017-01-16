package com.sam.jcc.cloud.dataprovider.impl;

import com.google.common.base.Charsets;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import com.sam.jcc.cloud.utils.project.DependencyManager;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 17-Jan-17
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        FileManager.class,
        ProjectParser.class,
        ZipArchiveManager.class,
        DependencyManager.class,
        MySqlDependencyInjector.class
})
public class MySqlDependencyInjectorTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Autowired
    FileManager files;
    @Autowired
    ZipArchiveManager manager;


    @Autowired
    MySqlDependencyInjector injector;

    @Test
    public void injects() throws IOException {
        final AppData app = mavenApp();
        injector.inject(app);

        assertThat(app.getSources()).isNotNull();

        final File sources = readSources(app);

        assertThat(readProperties(sources)).contains(
                "spring.datasource.url",
                "spring.datasource.username",
                "spring.datasource.password"
        );

        assertThat(readPom(sources)).contains("spring-boot-starter-data-jpa");
    }

    AppData mavenApp() throws IOException {
        final AppData data = new AppData();

        data.setAppName("project");
        data.setSources(files.read(getResource(getClass(), "/maven-project.zip")));
        return data;
    }

    File readSources(AppData app) throws IOException {
        final File zip = tmp.newFile(), dir = tmp.newFolder();

        files.write(app.getSources(), zip);
        manager.unzip(zip, dir);
        return dir;
    }

    String readProperties(File sources) {
        File config = new File(sources, "src/main/resources/application.properties");
        return new String(files.read(config), Charsets.UTF_8);
    }

    String readPom(File sources) {
        File config = new File(sources, "pom.xml");
        return new String(files.read(config), Charsets.UTF_8);
    }
}