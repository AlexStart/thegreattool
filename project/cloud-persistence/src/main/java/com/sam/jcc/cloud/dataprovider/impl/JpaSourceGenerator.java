package com.sam.jcc.cloud.dataprovider.impl;

import com.google.common.collect.ImmutableMap;
import com.sam.jcc.cloud.dataprovider.AppData;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 18.01.2017
 */
@Component
class JpaSourceGenerator extends AbstractSourceGenerator {

    protected static final String EXAMPLE_DTO = "${exampleDTO}";
    protected static final String EXAMPLE_SERVICE = "${exampleService}";
    protected static final String EXAMPLE_DAO = "${exampleDAO}";
    protected static final String EXAMPLE_CONVERTER = "${exampleConverter}";

    protected String dtoTemplate;
    protected String converterTemplate;
    protected String converterImplTemplate;
    protected String serviceTemplate;
    protected String serviceImplTemplate;
    protected String serviceTestTemplate;
    protected String restControllerImplTemplate;
    protected String testRestControllerTemplate;

    @PostConstruct
    public void setUp() {
        testRestControllerTemplate = read("/templates/example-rest-controller-test.java.txt");
        restControllerImplTemplate = read("/templates/example-rest-controller-impl.java.txt");
        dtoTemplate = read("/templates/example-dto.java.txt");
        serviceTemplate = read("/templates/example-service-interface.java.txt");
        serviceImplTemplate = read("/templates/example-service-impl.java.txt");
        serviceTestTemplate = read("/templates/example-service-test.java.txt");
        converterTemplate = read("/templates/example-converter-interface.java.txt");
        converterImplTemplate = read("/templates/example-converter-impl.java.txt");
        entityTemplate = read("/templates/example.java.txt");
        daoTemplate = read("/templates/example-dao.java.txt");
        testTemplate = read("/templates/example-dao-test.java.txt");
    }

    @Override
    protected void addDto(AppData app) {
        if(dtoTemplate == null) {
            return;
        }
        final String dto = apply(dtoTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app, "dto")
        ));

        final String path = format("{0}/ExampleDTO.java", pathToSources(app, "dto"));
        save(app.getLocation(), path, dto);
    }

    @Override
    protected void addService(AppData app) {
        addService(app, serviceTemplate, "service", "ExampleService");
        addService(app, serviceImplTemplate, "service.impl", "ExampleServiceImpl");
    }

    @Override
    protected void addServiceTest(AppData app) {
        if(serviceTestTemplate == null) {
            return;
        }
        final String test = apply(serviceTestTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app, "service"),
                EXAMPLE_DTO, getPackageImport (basePackage(app, "dto"), "ExampleDTO")
        ));

        final String path = format("{0}/ExampleServiceTest.java", pathToTests(app, "service"));
        save(app.getLocation(), path, test);
    }

    @Override
    protected void addConverter(AppData app) {
        addConverter(app, converterTemplate, "converter", "ExampleConverter");
        addConverter(app, converterImplTemplate, "converter.impl", "ExampleConverterImpl");
    }

    @Override
    protected void addRestController(AppData app) {
        if(restControllerImplTemplate == null) {
            return;
        }
        final String test = apply(restControllerImplTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app, "controller"),
                EXAMPLE_DTO, getPackageImport(basePackage(app, "dto"), "ExampleDTO"),
                EXAMPLE_SERVICE, getPackageImport(basePackage(app, "service"), "ExampleService")
        ));

        final String path = format("{0}/ExampleController.java", pathToSources(app, "controller"));
        save(app.getLocation(), path, test);
    }

    @Override
    protected void addRestControllerTest(AppData app) {
        if(testRestControllerTemplate == null) {
            return;
        }
        final String test = apply(testRestControllerTemplate, of(
                CREATED, formattedCurrentDate(),
                PACKAGE, basePackage(app, "controller"),
                EXAMPLE_DTO, getPackageImport(basePackage(app, "dto"), "ExampleDTO"),
                EXAMPLE_SERVICE, getPackageImport(basePackage(app, "service"), "ExampleService")
        ));

        final String path = format("{0}/ExampleControllerRestTest.java", pathToTests(app, "controller"));
        save(app.getLocation(), path, test);
    }

    private void addConverter(AppData app, String converterTemplate, String converterPackage, String converterName) {
        if(converterTemplate == null) {
            throw new IllegalArgumentException(String.format("The Template for '{0}' is not found.", converterName));
        }

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put(CREATED, formattedCurrentDate());
        templateParams.put(PACKAGE, basePackage(app, "converter"));
        templateParams.put(EXAMPLE, getPackageImport (basePackage(app, "persistence.entity"), "Example"));
        templateParams.put(EXAMPLE_DTO, getPackageImport (basePackage(app, "dto"), "ExampleDTO"));

        if("ExampleConverterImpl".equals(converterName)){
            templateParams.put(PACKAGE, basePackage(app, "converter.impl"));
            templateParams.put(EXAMPLE_CONVERTER, getPackageImport (basePackage(app, "converter"), "ExampleConverter"));
        }

        final String converter = apply(converterTemplate, ImmutableMap.copyOf(templateParams));

        final String path = format("{0}/{1}.java", pathToSources(app, transformPackageToPath(converterPackage)), converterName);
        save(app.getLocation(), path, converter);
    }

    private void addService(AppData app, String serviceTemplate, String servicePackage, String serviceName) {
        if(serviceTemplate == null) {
            throw new IllegalArgumentException(String.format("The Template for '{0}' is not found.", serviceName));
        }

        Map<String, String> templateParams = new HashMap<>();
        templateParams.put(CREATED, formattedCurrentDate());
        templateParams.put(PACKAGE, basePackage(app, servicePackage));
        templateParams.put(EXAMPLE_DTO, getPackageImport(basePackage(app, "dto"), "ExampleDTO"));

        if("ExampleServiceImpl".equals(serviceName)) {
            templateParams.put (EXAMPLE_SERVICE,  getPackageImport(basePackage(app, "service"), "ExampleService"));
            templateParams.put (EXAMPLE_CONVERTER,  getPackageImport(basePackage(app, "converter"), "ExampleConverter"));
            templateParams.put (EXAMPLE_DAO,  getPackageImport(basePackage(app, "persistence.repository"), "ExampleDAO"));
            templateParams.put (EXAMPLE,  getPackageImport(basePackage(app, "persistence.entity"), "Example"));
        }

        final String service = apply(serviceTemplate, ImmutableMap.copyOf(templateParams));

        final String path = format("{0}/{1}.java", pathToSources(app, transformPackageToPath(servicePackage)), serviceName);
        save(app.getLocation(), path, service);
    }
}
