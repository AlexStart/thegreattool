package com.sam.jcc.cloud.dataprovider.impl;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.utils.files.FileManager;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.collect.ImmutableMap.of;
import static com.sam.jcc.cloud.utils.files.FileManager.getResource;
import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 18.01.2017
 */
@Component
class SourceGenerator {

    private static final String DEFAULT_GROUP_ID = "com.sam.jcc.cloud";

    private static final String CREATED = "${created}";
    private static final String PACKAGE = "${package}";

    @Autowired
    private FileManager files;

    @Autowired
    private UnzipSandbox sandbox;

    private String daoTemplate;
    private String testTemplate;
    private String entityTemplate;

    @Setter
    @VisibleForTesting
    private String groupId = DEFAULT_GROUP_ID;

    @PostConstruct
    public void setUp() {
        entityTemplate = read("/templates/example.java.txt");
        daoTemplate = read("/templates/example-dao.java.txt");
        testTemplate = read("/templates/example-dao-test.java.txt");
    }

    public void generate(AppData app) {
        final byte[] updated = sandbox.apply(app.getSources(), modify(app));
        app.setSources(updated);
    }

    private Consumer<File> modify(AppData app) {
        return sources -> {
                    app.setLocation(sources);
                    addEntity(app);
                    addDao(app);
                    addTest(app);
                };
    }

    private void addEntity(AppData app) {
        final String entity = apply(entityTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app) + "entity"
        ));

        final String path = format("{0}/entity/Example.java", pathToSources(app));
        save(app.getLocation(), path, entity);
    }

    private void addDao(AppData app) {
        final String dao = apply(daoTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app) + "repository"
        ));

        final String path = format("{0}/repository/ExampleDAO.java", pathToSources(app));
        save(app.getLocation(), path, dao);
    }

    private void addTest(AppData app) {
        final String test = apply(testTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app) + "repository"
        ));

        final String path = format("{0}/repository/ExampleDAOTest.java", pathToTests(app));
        save(app.getLocation(), path, test);
    }

    private String basePackage(AppData app) {
        return format("{0}.{1}.persistence.", groupId, app.getAppName());
    }

    private String pathToSources(AppData app) {
        return format("src/main/java/{0}/{1}/persistence/", transformPackageToPath(groupId), app.getAppName());
    }

    private String pathToTests(AppData app) {
        return format("src/test/java/{0}/{1}/persistence/", transformPackageToPath(groupId), app.getAppName());
    }

    private String transformPackageToPath(String value) {
        return value.replaceAll("\\.", "/");
    }

    private String apply(String template, Map<String, String> properties) {
        String result = template;

        for (val property : properties.entrySet()) {
            result = result.replace(property.getKey(), property.getValue());
        }
        return result;
    }

    private void save(File root, String path, String sources) {
        final File testFile = new File(root, path);
        files.createFile(testFile);
        files.write(sources.getBytes(), testFile);
    }

    private String formattedCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }

    private String read(String resource) {
        return files.toString(getResource(getClass(), resource));
    }
}
