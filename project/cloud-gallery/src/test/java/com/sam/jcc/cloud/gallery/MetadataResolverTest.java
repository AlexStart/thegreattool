package com.sam.jcc.cloud.gallery;

import lombok.Data;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
public class MetadataResolverTest {

    private MetadataResolver resolver = new MetadataResolver();

    @Test
    //TODO: need spec
    public void resolves() {
        final Map<String, Object> type = resolver.resolve(testData());

        assertThat(type).containsOnlyKeys(
                "strField",
                "intField",
                "setField",
                "mapField",
                "dateField",
                "nullField"
        );
    }


    TestData testData() {
        final TestData data = new TestData();

        data.strField = "str";
        data.intField = 12345;
        data.dateField = new Date();
        data.setField = singleton(45.0d);
        data.mapField = singletonMap("realField", singletonList(new Date()));
        return data;
    }

    @Data
    public static class TestData {

        private Date dateField;
        private String strField;
        private Integer intField;
        private Map<String, List<Date>> mapField;
        private Set<Double> setField;
        private Long nullField;
    }
}