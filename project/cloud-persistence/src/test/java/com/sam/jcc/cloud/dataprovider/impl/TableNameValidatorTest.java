package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.impl.TableNameValidator.TableNameValidationException;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 16-Jan-17
 */
public class TableNameValidatorTest {

    static final String VALID_TABLE_NAME = "table$77_";

    TableNameValidator validator = new TableNameValidator();

    @Test
    public void validates() {
        validator.validate(VALID_TABLE_NAME);
    }

    @Test(expected = TableNameValidationException.class)
    public void failsOnInjections() {
        validator.validate("table; create");
    }

    @Test(expected = TableNameValidationException.class)
    public void failsOnMultiline() {
        validator.validate("table_name\ncreate");
    }

    @Test(expected = TableNameValidationException.class)
    public void failOnEmpty() {
        validator.validate("");
    }
}