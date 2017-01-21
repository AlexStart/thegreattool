package com.sam.jcc.cloud.providers;

import com.sam.jcc.cloud.app.AppMetadata;
import com.sam.jcc.cloud.app.AppProvider;
import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.utils.project.ArtifactIdValidator.AppMetadataValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppProviderTest {

    @Autowired
    AppProvider appProvider;

    IAppMetadata project;

    @Before
    public void setUp() {
        project = new AppMetadata();
        project.setProjectName("Test-Project");
    }

    @After
    public void tearDown(){
        appProvider
                .findAll()
                .forEach(appProvider::delete);
    }

    @Test
    public void integrationTest() {
        assertThat(appProvider.findAll()).isEmpty();
        appProvider.create(project);
        assertThat(appProvider.findAll()).hasSize(1);

        project = appProvider.read(project);
        assertThat(project.getProjectName()).isEqualTo("test-project");

        appProvider.delete(project);
        assertThat(appProvider.findAll()).isEmpty();
    }

    @Test(expected = UnsupportedCallException.class)
    public void failsOnUpdate(){
        appProvider.create(project);
        appProvider.update(project);
    }

    @Test(expected = AppMetadataValidationException.class)
    public void failsOnEmptyName() {
        appProvider.preprocess(new AppMetadata());
    }

    @Test(expected = AppMetadataValidationException.class)
    public void failsOnNotValid() {
        final AppMetadata metadata = new AppMetadata();
        metadata.setProjectName("Not valid name");
        appProvider.preprocess(metadata);
    }
}
