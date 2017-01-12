package com.sam.jcc.cloud.gallery;

import com.sam.jcc.cloud.TranslationResolver;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        MetadataResolver.class,
        TranslationResolver.class
})
public class MetadataResolverTest {

    @Autowired
    MetadataResolver resolver;

    @Test
    public void resolves() {
        final Map<String, Object> type = resolver.resolve(testData());

        assertThat(type).containsOnlyKeys(
                "class",
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