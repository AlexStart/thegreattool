package com.sam.jcc.cloud.gallery;

import lombok.Data;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
public class MetadataResolverTest {

    private MetadataResolver resolver = new MetadataResolver();

    @Test
    public void resolves() {
        final Map<String, Object> type = resolver.resolve(testData());

        assertThat(type).contains(
                entry("strField", "string"),
                entry("intField", "number"),
                entry("dateField", "date")
        );
        assertThat(type.get("mapField")).isNotNull();
        assertThat(type.get("setField")).isEqualTo(singletonMap(0, "number"));
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