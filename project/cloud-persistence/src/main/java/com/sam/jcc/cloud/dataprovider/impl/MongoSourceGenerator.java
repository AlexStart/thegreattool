package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.google.common.collect.ImmutableMap.of;
import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 23-Jan-17
 */
@Component
class MongoSourceGenerator extends AbstractSourceGenerator {

    protected String testDataTemplate;

    @PostConstruct
    public void setUp() {
        entityTemplate = read("/templates/example-mongo.java.txt");
        daoTemplate = read("/templates/example-dao-mongo.java.txt");
        testTemplate = read("/templates/example-dao-test.java.txt");
        testDataTemplate = read("/templates/example-test-data-rest.java.txt");
    }

    @Override
    protected void addTestDataRest(AppData app) {
        if(testDataTemplate == null) {
            return;
        }
        final String test = apply(testDataTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app, "rest"),
                EXAMPLE, getPackageImport(basePackage(app, "persistence.entity"), "Example"),
                EXAMPLE_DAO, getPackageImport(basePackage(app, "persistence.repository"), "ExampleDAO")
        ));

        final String path = format("{0}/ExampleDataRestTest.java", pathToTests(app, "rest"));
        save(app.getLocation(), path, test);
    }

    @Override
    protected void addDto(AppData app) {
        return;
    }

    @Override
    protected void addService(AppData app) {
        return;
    }

    @Override
    protected void addServiceTest(AppData app) {
        return;
    }

    @Override
    protected void addConverter(AppData app) {
        return;
    }
}
