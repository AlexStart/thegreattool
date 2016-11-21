package com.sam.jcc.cloud.persistence;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Alexey Zhytnik
 * @since 18.11.2016
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.sam.jcc.cloud.persistence")
@PropertySource("classpath:database.properties")
@EnableJpaRepositories("com.sam.jcc.cloud.persistence")
class DatabaseConfiguration {

    @Resource
    Environment env;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setUrl(env.getRequiredProperty("db.hibernate.url"));
        ds.setUsername(env.getRequiredProperty("db.hibernate.user"));
        ds.setPassword(env.getRequiredProperty("db.hibernate.password"));
        ds.setDriverClassName(env.getRequiredProperty("db.hibernate.driver"));
        return ds;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        final JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource());
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setPackagesToScan("com.sam.jcc.cloud.persistence");
        factory.setJpaProperties(getHibernateProperties());
        return factory;
    }

    private Properties getHibernateProperties() {
        final Properties props = new Properties();

        props.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        props.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
        props.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
        return props;
    }
}
