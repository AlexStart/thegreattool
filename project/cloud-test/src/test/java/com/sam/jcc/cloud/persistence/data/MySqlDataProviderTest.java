package com.sam.jcc.cloud.persistence.data;

import static com.sam.jcc.cloud.utils.files.FileManager.getResource;

import java.io.File;
import java.util.Random;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.dataprovider.ProjectSourcesNotFound;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDataProvider;
import com.sam.jcc.cloud.dataprovider.impl.MySqlDatabaseManager;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.utils.files.FileManager;

/**
 * @author Alexey Zhytnik
 * @since 17.01.2017
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MySqlDataProviderTest {

    static final String PROJECT_NAME = "project" + new Random().nextInt(1000);

    @Autowired
    MySqlDataProvider mysqlProvider;

    @Test(expected = UnsupportedCallException.class)
    public void createOperationIsNotSupported() {
        mysqlProvider.create(new AppData());
    }

    @Test(expected = ProjectDataNotFoundException.class)
    public void failsOnUnknownApp() {
        mysqlProvider.update(unknown());
    }

    @Test(expected = InternalCloudException.class)
    public void failsOnAlreadyDatabaseInjectedApp() {
        mysqlProvider.update(projectWithDatabase());
    }

    @Test(expected = ProjectSourcesNotFound.class)
    public void failsOnAppWithoutSources() {
        mysqlProvider.update(projectWithoutSources());
    }

    @Test
    public void updates() {
        final AppData app = mavenProject();
        try {
            mysqlProvider.update(app);
        } finally {
            dbManager.drop(app);
        }
    }

    AppData unknown() {
        final AppData unknown = new AppData();
        unknown.setAppName("project");
        return unknown;
    }

    AppData mavenProject() {
        return mavenProject(true, false);
    }

    AppData projectWithDatabase() {
        return mavenProject(true, true);
    }

    AppData projectWithoutSources() {
        return mavenProject(false, false);
    }



    /* TEST INFRASTRUCTURE */

    @Autowired
    MySqlDatabaseManager dbManager;

    @Autowired
    ProjectDataRepository repository;

    AppData mavenProject(boolean hasSources, boolean dataIncluded) {
        final ProjectData data = new ProjectData();
        data.setName(PROJECT_NAME);
        data.setVcsName("project-vcs");
        data.setJobName("project-ci");
        data.setDataSupport(dataIncluded);

        if (hasSources) {
            final File sources = getResource(getClass(), "/maven-project.zip");
            data.setSources(new FileManager().read(sources));
        }

        repository.save(data);

        final AppData app = new AppData();
        app.setAppName(PROJECT_NAME);
        return app;
    }

    @After
    public void tearDown() {
        repository.findByName(PROJECT_NAME).ifPresent(repository::delete);
    }
}
