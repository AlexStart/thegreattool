package com.sam.jcc.cloud;

import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 14.12.2016
 */
public class ExceptionTranslationResolverTest {

    static final String EXIST_KEY = "vcs.unknown.protocol";

    ExceptionTranslationResolver resolver = new ExceptionTranslationResolver("error-test.yml");

    @Test
    public void translates() {
        assertThat(resolver.getValue("internal.system.unknown")).isNotEmpty();
    }

    @Test
    public void checksLocale() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        assertThat(resolver.getValue(EXIST_KEY)).isEqualTo("Unknown protocol '{0}'");

        LocaleContextHolder.setLocale(Locale.forLanguageTag("ru"));
        assertThat(resolver.getValue(EXIST_KEY)).isEqualTo("Неизвестный протокол '{0}'");
    }

    @Test
    public void returnsEmptyOnUnknown() {
        assertThat(resolver.getValue("some key")).isEmpty();
    }
}