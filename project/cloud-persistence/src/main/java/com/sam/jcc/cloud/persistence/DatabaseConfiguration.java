package com.sam.jcc.cloud.persistence;

import com.sam.jcc.cloud.PropertyResolver;
import com.sam.jcc.cloud.PropertyResolverHelper;
import org.flywaydb.core.Flyway;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

/**
 * @author Alexey Zhytnik
 * @since 18.11.2016
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.sam.jcc.cloud.persistence")
@EnableJpaRepositories("com.sam.jcc.cloud.persistence")
public class DatabaseConfiguration {

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setUrl(property("db.hibernate.url"));
        ds.setUsername(property("db.hibernate.user"));
        ds.setPassword(property("db.hibernate.password"));
        ds.setDriverClassName(property("db.hibernate.driver"));
        return ds;
    }

    @Bean
    public JpaTransactionManager transactionManager(DataSource dataSource) {
        final JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory(dataSource).getObject());
        return txManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setPackagesToScan("com.sam.jcc.cloud.persistence");
        factory.setJpaProperties(jpaProperties());
        return factory;
    }

    @Bean
    public JdbcTemplate mySqlJdbcTemplate() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setUrl(getMySqlJdbcUrl());
        ds.setUsername(property("db.mysql.user"));
        ds.setPassword(property("db.mysql.password"));
        ds.setDriverClassName(property("db.mysql.driver"));

        return new JdbcTemplate(ds);
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        final Flyway flyway = new Flyway();

        flyway.setInitOnMigrate(true);
        flyway.setDataSource(dataSource);
        return flyway;
    }

    private Properties jpaProperties() {
        final Properties props = new Properties();

        props.put("hibernate.dialect", property("db.hibernate.dialect"));
        props.put("hibernate.show_sql", property("db.hibernate.show_sql"));
        props.put("hibernate.hbm2ddl.auto", property("db.hibernate.hbm2ddl.auto"));
        return props;
    }

    private String property(String key) {
        return PropertyResolver.getProperty(key);
    }

    private String getMySqlJdbcUrl() {
        return PropertyResolverHelper.
                getConnectionUrl(getProperty("db.mysql.protocol"), getProperty("db.mysql.host"),getProperty("db.mysql.port"));
    }
}
