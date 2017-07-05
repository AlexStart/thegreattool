package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.data.IDataInjector;
import com.sam.jcc.cloud.utils.files.FileManager;
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
class MySqlInjector implements IDataInjector<AppData> {

    private static final String JPA = "spring.datasource";

    @Autowired
    private FileManager files;

    @Autowired
    private UnzipSandbox sandbox;

    @Autowired
    private ProjectParser parser;

    @Autowired
    private DependencyManager dependencyManager;

    @Autowired
    private TableNameValidator validator;

    public void inject(AppData data) {
        final byte[] updated = sandbox.apply(data.getSources(), sources -> inject(data, sources));
        data.setSources(updated);
    }

    private void inject(AppData data, File sources) {
        dependencyManager.add(sources, lombok());
        dependencyManager.add(sources, dataJpaStarter());
        dependencyManager.add(sources, mysqlConnector());

        final File properties = parser.getPropertiesFile(sources);
        files.append(lineSeparator().getBytes(), properties);
        files.append(settings(data).getBytes(), properties);
    }

    private Dependency dataJpaStarter() {
        final Dependency jpa = new Dependency();

        jpa.setScope("compile");
        jpa.setGroupId("org.springframework.boot");
        jpa.setArtifactId("spring-boot-starter-data-jpa");
        return jpa;
    }

    private Dependency mysqlConnector() {
        final Dependency mysql = new Dependency();

        mysql.setScope("compile");
        mysql.setGroupId("mysql");
        mysql.setArtifactId("mysql-connector-java");
        return mysql;
    }

    //TODO: transfer
    private Dependency lombok() {
        final Dependency lombok = new Dependency();

        lombok.setScope("compile");
        lombok.setGroupId("org.projectlombok");
        lombok.setArtifactId("lombok");
        return lombok;
    }

    private String settings(AppData data) {
        return String.join(lineSeparator(),
                of(
                        jpa("url", String.format(getProperty("db.mysql.url.full.pattern"), validator.getValidTableName(data.getAppName()))),
                        jpa("username", getProperty("db.mysql.user")),
                        jpa("password", getProperty("db.mysql.password")),
                        "spring.jpa.hibernate.ddl-auto=update",
                        "rest.exception.message=something goes wrong on the server",
                        "rest.content.type=application/json;charset=UTF-8"
                ));
    }

    private String jpa(String property, String value) {
        return format("%s.%s=%s", JPA, property, value);
    }

}
