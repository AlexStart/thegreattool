package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.exception.BusinessCloudException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Component
class TableNameValidator {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("[0-9a-zA-Z$_]+");

    public void validate(String name) {
        if (!TABLE_NAME_PATTERN.matcher(name).find()) {
            throw new TableNameValidationException(name);
        }
    }

    public static class TableNameValidationException extends BusinessCloudException {
        public TableNameValidationException(String name) {
            super("persistence.database.validation.error", name);
        }
    }
}
