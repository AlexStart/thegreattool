package com.sam.jcc.cloud.i;

import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 17.11.2016
 */
public class TranslationResolverTest {

    TranslationResolver resolver = new TranslationResolver();

    @Before
    public void setUp() {
        resolver.setResource("translations-test.yml");
        resolver.setUp();
    }

    @Test
    public void containsProviderNames() {
        final Map<String, String> names = resolver.getNames(Provider.class);

        assertThat(names).containsOnly(
                entry("ru", "rus_name"),
                entry("en", "eng_name")
        );
    }

    @Test
    public void containsProviderDescriptions() {
        final Map<String, String> descriptions = resolver.getDescriptions(Provider.class);

        assertThat(descriptions).containsOnly(
                entry("ru", "rus_desc"),
                entry("en", "eng_desc")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsOnUnknownType() {
        final TranslationResolver resolver = new TranslationResolver();
        resolver.setResource("translations-wrong.yml");
        resolver.setUp();
    }

    Entry<String, String> entry(String lang, String value) {
        return new SimpleEntry<>(lang, value);
    }

    static abstract class Provider extends AbstractProvider {
        public Provider(List list) {
            super(list);
        }
    }
}