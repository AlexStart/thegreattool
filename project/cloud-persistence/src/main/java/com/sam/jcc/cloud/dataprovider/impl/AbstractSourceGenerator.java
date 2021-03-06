package com.sam.jcc.cloud.dataprovider.impl;

import static com.google.common.collect.ImmutableMap.of;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.utils.files.FileManager.getResourceAsBytes;
import static java.text.MessageFormat.format;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.data.ISourceGenerator;
import com.sam.jcc.cloud.utils.files.FileManager;

import lombok.Setter;
import lombok.val;

/**
 * @author Alexey Zhytnik
 * @since 23-Jan-17
 */
class AbstractSourceGenerator implements ISourceGenerator<AppData> {

    private static final String CREATED = "${created}";
    private static final String PACKAGE = "${package}";
    private static final String EXAMPLE = "${example}";

    @Autowired
    private FileManager files;

    @Autowired
    private UnzipSandbox sandbox;

    protected String daoTemplate;
    protected String testTemplate;
    protected String entityTemplate;

    @Setter
    @VisibleForTesting
    private String groupId = getProperty("data.template.groupId");

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
                PACKAGE, basePackage(app) + "repository",
                EXAMPLE, basePackage(app) + "entity.Example"
        ));

        final String path = format("{0}/repository/ExampleDAO.java", pathToSources(app));
        save(app.getLocation(), path, dao);
    }

    private void addTest(AppData app) {
        final String test = apply(testTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app) + "repository",
                EXAMPLE, basePackage(app) + "entity.Example"
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

    protected String read(String resource) {
        return new String(getResourceAsBytes(getClass(), resource), Charsets.UTF_8);
    }
}
