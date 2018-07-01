package com.soze.idlekluch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class JPAConfiguration {

  @Value("${IDLE_KLUCH_DATABASE_PASSWORD}")
  private String dbPassword;

  @Value("${IDLE_KLUCH_DATABASE_NAME}")
  private String dbName;

  @Value("${IDLE_KLUCH_DATABASE_USERNAME}")
  private String dbUsername;

  @Bean
  public DataSource dataSource() throws SQLException {
    final DriverManagerDataSource driver = new DriverManagerDataSource();
    driver.setDriverClassName("org.postgresql.Driver");
    driver.setUrl("jdbc:postgresql://localhost:5432/" + dbName.trim() + "?stringtype=unspecified");
    driver.setUsername(dbUsername.trim());
    driver.setPassword(dbPassword.trim());
    return driver;
  }

//  @Bean
//  public EntityManagerFactory entityManagerFactory() throws SQLException {
//    final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//    vendorAdapter.setGenerateDdl(false);
//    vendorAdapter.setShowSql(true);
//
//    final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//    factory.setJpaVendorAdapter(vendorAdapter);
//    factory.setPackagesToScan("com.soze.idlekluch");
//    factory.setDataSource(dataSource());
//    factory.afterPropertiesSet();
//
//    return factory.getObject();
//  }

  @Bean
  public EntityManager getEntityManager(final EntityManagerFactory entityManagerFactory) {
    return entityManagerFactory.createEntityManager();
  }

  @Bean
  public PlatformTransactionManager transactionManager() throws SQLException {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
    return transactionManager;
  }

  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() throws SQLException {
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.schema-generation.database.action", "none");
    properties.put("hibernate.order_inserts", true);
    properties.put("hibernate.order_updates", true);
    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect"); //you can change this if you have a different DB
    adapter.setGenerateDdl(false);
    adapter.setShowSql(false);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(adapter);
    factory.setDataSource(dataSource());
    factory.setPackagesToScan("com.soze.idlekluch");
    factory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
    factory.setValidationMode(ValidationMode.NONE);
    factory.setJpaPropertyMap(properties);
    return factory;
  }

}
