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
 * @since 22-Jan-17
 */
@Setter
@Component
class MongoDbInjector implements IDataInjector<AppData> {

    private static final String MONGO = "spring.data.mongodb";

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
        dependencyManager.add(sources, dataMongoDbStarted());
        dependencyManager.add(sources, dataRestStarted());

        final File properties = parser.getPropertiesFile(sources);
        files.append(lineSeparator().getBytes(), properties);
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

    private Dependency dataRestStarted() {
        final Dependency mongo = new Dependency();

        mongo.setScope("compile");
        mongo.setVersion("1.4.3.RELEASE");
        mongo.setGroupId("org.springframework.boot");
        mongo.setArtifactId("spring-boot-starter-data-rest");
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
                validator.getValidTableName(data.getAppName())
        );

        if (getProperty("db.mongo.user").isEmpty()) {
            return String.join(lineSeparator(),
                    of(
                            mongo("uri", uri),
                            mongo("repository.rest.collection", getProperty("data.rest.collection")),
                            mongo("repository.rest.path", getProperty("data.rest.path")),
                            mongo("test.rest.url", getProperty("test.rest.url")),
                            mongo("test.rest.content.type", getProperty("test.rest.content.type")),
                            mongo("test.rest.entity.id", getProperty("test.rest.entity.id"))
                    ));
        }
        return settingsWithAuth(uri);
    }

    private String settingsWithAuth(String uri) {
        return String.join(lineSeparator(),
                of(
                        mongo("uri", uri),
                        mongo("username", getProperty("db.mongo.user")),
                        mongo("password", getProperty("db.mongo.password")),
                        mongo("repository.rest.collection", getProperty("repository.rest.collection")),
                        mongo("repository.rest.path", getProperty("repository.rest.path")),
                        mongo("test.rest.url", getProperty("test.rest.url")),
                        mongo("test.rest.content.type", getProperty("test.rest.content.type")),
                        mongo("test.rest.entity.id", getProperty("test.rest.entity.id"))
                ));
    }

    private String mongo(String property, String value) {
        return format("%s.%s=%s", MONGO, property, value);
    }
}
