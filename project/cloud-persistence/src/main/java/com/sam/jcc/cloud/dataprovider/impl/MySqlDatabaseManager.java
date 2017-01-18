package com.sam.jcc.cloud.dataprovider.impl;

import com.sam.jcc.cloud.dataprovider.AppData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Alexey Zhytnik
 * @since 16.01.2017
 */
@Component
public class MySqlDatabaseManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TableNameValidator validator;

    public void create(AppData data) {
        final String name = data.getAppName();
        validator.validate(name);

        jdbcTemplate.execute("CREATE DATABASE " + name);
    }

    public void drop(AppData data) {
        final String name = data.getAppName();
        validator.validate(name);

        jdbcTemplate.execute("DROP DATABASE IF EXISTS " + name);
    }
}
