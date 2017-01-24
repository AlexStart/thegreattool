package com.sam.jcc.cloud.dataprovider.impl;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Alexey Zhytnik
 * @since 18.01.2017
 */
@Component
class JpaSourceGenerator extends AbstractSourceGenerator {

    @PostConstruct
    public void setUp() {
        entityTemplate = read("/templates/example.java.txt");
        daoTemplate = read("/templates/example-dao.java.txt");
        testTemplate = read("/templates/example-dao-test.java.txt");
    }
}
