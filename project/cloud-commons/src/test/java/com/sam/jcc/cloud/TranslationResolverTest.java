package com.sam.jcc.cloud;

import com.sam.jcc.cloud.TranslationResolver;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.provider.AbstractProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.entry;

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

    static abstract class Provider extends AbstractProvider<IProjectMetadata> {
        public Provider(List<IEventManager<IProjectMetadata>> list) {
            super(list);
        }
    }
}