package com.sam.jcc.cloud.dataprovider.impl;

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
    }
}
