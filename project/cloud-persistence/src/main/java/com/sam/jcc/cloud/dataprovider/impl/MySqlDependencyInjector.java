package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.project.DependencyManager;
import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Component
class MySqlDependencyInjector {

    private static final String JPA = "spring.datasource";

    @Autowired
    private FileManager files;

    @Autowired
    private ZipArchiveManager zipManager;

    @Autowired
    private DependencyManager dependencyManager;

    public void inject(AppData data) {
        try (TempFile tmp = files.createTempFile(); TempFile sources = files.createTempDir()) {
            files.write(data.getSources(), tmp);

            zipManager.unzip(tmp, sources);
            dependencyManager.add(sources, dataJpaStarter());
            data.setSources(zipManager.zip(sources));
        }
    }

    private Dependency dataJpaStarter() {
        final Dependency jpa = new Dependency();

        jpa.setScope("compile");
        jpa.setVersion("1.4.3.RELEASE");
        jpa.setGroupId("org.springframework.boot");
        jpa.setArtifactId("spring-boot-starter-data-jpa");
        return jpa;
    }

    private List<String> properties(String name) {
        return of(
                jpa("uri", getProperty("data.template.jpa.url") + name),
                jpa("username", getProperty("data.template.jpa.username")),
                jpa("password", getProperty("data.template.jpa.password"))
        );
    }

    private String jpa(String property, String value) {
        return format("%s.%s=%s", JPA, property, value);
    }
}
