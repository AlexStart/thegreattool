package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 18-Dec-16
 */
public class JenkinsConfigurationBuilderTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    CIProject project;

    JenkinsConfigurationBuilder builder;

    @Before
    public void setUp() throws Exception {
        final ItemStorage<CIProject> storage = new ItemStorage<>(CIProject::getName);
        storage.setRoot(temp.newFolder());

        project = new CIProject();
        project.setArtifactId("TempProject");
        copyProjectSourcesIntoStorage(storage.create(project));

        builder = new JenkinsConfigurationBuilder(storage);
    }

    @Test
    public void configures() {
        assertThat(builder.build(project)).isNotNull().isNotEmpty();
    }

    void copyProjectSourcesIntoStorage(File dir) throws Exception {
        final File sources = new ClassPathResource("/app.zip").getFile();
        new ZipArchiveManager().unzip(sources, dir);
    }
}