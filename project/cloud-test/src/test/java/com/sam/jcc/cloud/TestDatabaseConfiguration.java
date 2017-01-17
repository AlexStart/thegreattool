package com.sam.jcc.cloud;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.util.Properties;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

/**
 * @author Alexey Zhytnik
 * @since 08.12.2016
 */
@Configuration
public class TestDatabaseConfiguration {

    public static final String H2_TEST = "!MySQL-Test";
    public static final String MySQL_TEST = "MySQL-Test";

    @Bean
    @Primary
    @Profile(H2_TEST)
    public DataSource inMemoryDataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        ds.setDriverClassName("org.h2.Driver");
        return ds;
    }

    @Bean
    @Profile(MySQL_TEST)
    public DataSource mySqlDataSource() {
        final MysqlDataSource ds = new MysqlDataSource();

        ds.setUrl(getProperty("test.mysql.url"));
        ds.setUser(getProperty("test.mysql.user"));
        ds.setPassword(getProperty("test.mysql.password"));
        return ds;
    }

    @Bean
    @Profile(MySQL_TEST)
    public Properties hibernateProperties() {
        final Properties props = new Properties();

        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return props;
    }
}
