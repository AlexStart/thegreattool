package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Alexey Zhytnik
 * @since 23-Jan-17
 */
@Component
class MongoSourceGenerator extends AbstractSourceGenerator {

    @PostConstruct
    public void setUp() {
        entityTemplate = read("/templates/example-mongo.java.txt");
        daoTemplate = read("/templates/example-dao-mongo.java.txt");
        testTemplate = read("/templates/example-dao-test.java.txt");
        testPropertyFileTemplate = read("/templates/mongo-test.properties.txt");
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

    @Override
    protected void addRestController(AppData app) {
        return;
    }

    @Override
    protected void addRestControllerTest(AppData app) {
        return;
    }

    @Override
    protected void addTestPropertyFile(AppData app) {
        final String pathToTestPropertyFile = "src/test/resources/mysql-test.properties";
        save(app.getLocation(), pathToTestPropertyFile, testPropertyFileTemplate);
    }
}
