package com.sam.jcc.cloud.provider;

import static java.util.Objects.isNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.TranslationResolver;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.IProvider;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
@Component
class TranslationProviderProcessor implements BeanPostProcessor {

    @Autowired
    private TranslationResolver translations;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof AbstractProvider) {
            setUpTranslations((AbstractProvider<?>) bean);
        }
        return bean;
    }

    private void setUpTranslations(AbstractProvider<?> provider) {
        final Map<String, String> names = translations.getNames(getProviderClass(provider));
        final Map<String, String> descriptions = translations.getDescriptions(getProviderClass(provider));

        if (containsWrongTranslation(names, descriptions)) {
            throw new TranslationNotFoundException(provider);
        }
        provider.setNames(names);
        provider.setDescriptions(descriptions);
    }

    private boolean containsWrongTranslation(Map<String, String> names, Map<String, String> descriptions) {
        return isNull(names) || isNull(descriptions) ||
                names.isEmpty() || descriptions.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private Class<? extends IProvider<?>> getProviderClass(AbstractProvider<?> provider) {
        return (Class<? extends IProvider<?>>) provider.getClass();
    }

    public static class TranslationNotFoundException extends InternalCloudException {
        public TranslationNotFoundException(AbstractProvider<?> provider) {
            super("translation.notFound", provider);
        }
    }
}
