package com.sam.jcc.cloud;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author Alexey Zhytnik
 * @since 08.12.2016
 */
@Configuration
public class TestDatabaseConfiguration {

    @Bean
    @Primary
    public DataSource inMemoryDataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        ds.setDriverClassName("org.h2.Driver");
        return ds;
    }
}
