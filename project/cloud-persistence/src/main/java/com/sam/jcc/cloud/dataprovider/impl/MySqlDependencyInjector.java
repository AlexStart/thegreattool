package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import com.sam.jcc.cloud.utils.project.DependencyManager;
import com.sam.jcc.cloud.utils.project.DependencyManager.Dependency;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.google.common.collect.ImmutableList.of;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Setter
@Component
class MySqlDependencyInjector {

    private static final String JPA = "spring.datasource";

    @Autowired
    private FileManager files;

    @Autowired
    private ProjectParser parser;

    @Autowired
    private ZipArchiveManager zipManager;

    @Autowired
    private DependencyManager dependencyManager;

    public void inject(AppData data) {
        try (TempFile tmp = files.createTempFile(); TempFile sources = files.createTempDir()) {
            files.write(data.getSources(), tmp);

            zipManager.unzip(tmp, sources);
            dependencyManager.add(sources, dataJpaStarter());

            final File properties = parser.getPropertiesFile(sources);
            files.append(settings(data).getBytes(), properties);

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

    private String settings(AppData data) {
        return String.join(lineSeparator(),
                of(
                        jpa("url", getProperty("db.mysql.url") + data.getAppName()),
                        jpa("username", getProperty("db.mysql.user")),
                        jpa("password", getProperty("db.mysql.password"))
                ));
    }

    private String jpa(String property, String value) {
        return format("%s.%s=%s", JPA, property, value);
    }
}
