package com.sam.jcc.cloud.exception;

import com.sam.jcc.cloud.ExceptionTranslationResolver;

import java.text.MessageFormat;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
public abstract class CloudException extends RuntimeException {

    protected CloudException(String key, Object... args) {
        super(translateAndFill(key, args));
    }

    protected CloudException(Throwable cause, String key, Object... args) {
        super(translateAndFill(key, args), cause);
    }

    private static String translateAndFill(String key, Object[] args) {
        final String translation = ExceptionTranslationResolver.getTranslation(key);

        if (args.length != 0) {
            return MessageFormat.format(translation, args);
        }
        return translation;
    }
}
