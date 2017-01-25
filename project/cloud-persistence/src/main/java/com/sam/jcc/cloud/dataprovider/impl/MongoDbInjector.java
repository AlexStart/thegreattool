package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
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
 * @since 22-Jan-17
 */
@Setter
@Component
class MongoDbInjector {

    private static final String MONGO = "spring.data.mongodb";

    @Autowired
    private FileManager files;

    @Autowired
    private UnzipSandbox sandbox;

    @Autowired
    private ProjectParser parser;

    @Autowired
    private DependencyManager dependencyManager;

    public void inject(AppData data) {
        final byte[] updated = sandbox.apply(data.getSources(), sources -> inject(data, sources));
        data.setSources(updated);
    }

    private void inject(AppData data, File sources) {
        dependencyManager.add(sources, lombok());
        dependencyManager.add(sources, dataMongoDbStarted());

        final File properties = parser.getPropertiesFile(sources);
        files.append(settings(data).getBytes(), properties);
    }

    private Dependency dataMongoDbStarted() {
        final Dependency mongo = new Dependency();

        mongo.setScope("compile");
        mongo.setVersion("1.4.3.RELEASE");
        mongo.setGroupId("org.springframework.boot");
        mongo.setArtifactId("spring-boot-starter-data-mongodb");
        return mongo;
    }

    //TODO: transfer
    private Dependency lombok() {
        final Dependency lombok = new Dependency();

        lombok.setScope("compile");
        lombok.setVersion("1.16.10");
        lombok.setGroupId("org.projectlombok");
        lombok.setArtifactId("lombok");
        return lombok;
    }

    private String settings(AppData data) {
        final String uri = String.format("mongodb://%s:%s/%s",
                getProperty("db.mongo.host"),
                getProperty("db.mongo.port"),
                data.getAppName()
        );

        if (getProperty("db.mongo.user").isEmpty()) {
            return mongo("uri", uri);
        }
        return settingsWithAuth(uri);
    }

    private String settingsWithAuth(String uri) {
        return String.join(lineSeparator(),
                of(
                        mongo("uri", uri),
                        mongo("username", getProperty("db.mongo.user")),
                        mongo("password", getProperty("db.mongo.password"))
                ));
    }

    private String mongo(String property, String value) {
        return format("%s.%s=%s", MONGO, property, value);
    }
}
