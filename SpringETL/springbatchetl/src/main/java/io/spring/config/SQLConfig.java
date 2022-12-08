package io.spring.config;

import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
//@EnableTransactionManagement
@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager", basePackages = "io.spring.sqlrepo")
public class SQLConfig {

	@Autowired
	private Environment env;

	@Bean
//	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setPackagesToScan(new String[] { "io.spring.sqlmodel" });
//		factoryBean.setPersistenceUnitName("Person");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factoryBean.setJpaVendorAdapter(vendorAdapter);
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
		properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
		factoryBean.setJpaPropertyMap(properties);

		return factoryBean;
//		return builder.dataSource(dataSource).packages("io.spring.sqlrepo").build();
	}

	@Bean
//	@Primary
//	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
//				"jdbc:mysql://localhost:3306/spring_etl");
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
//		dataSource.setUsername("root");
//		dataSource.setPassword("Eliasroad1!");
		return dataSource;
//		return DataSourceBuilder.create().build();
	}

	@Bean
//	@Primary
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager manager = new JpaTransactionManager();
		manager.setEntityManagerFactory(entityManagerFactory().getObject());
		return manager;
//		return new JpaTransactionManager(entityManagerFactory);
	}

//	@Bean
////	@Primary
//	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
//		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//		factoryBean.setDataSource(dataSource());
//		factoryBean.setPackagesToScan(new String[] {"io.spring.model"});
//		factoryBean.setPersistenceUnitName("Person");
//		
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		factoryBean.setJpaVendorAdapter(vendorAdapter);
//		factoryBean.setJpaProperties(additionalProperties());
//		
//		return factoryBean;
//	}

//	@Bean
//	public PersistenceExceptionTranslationPostProcessor excTranslationPostProcessor() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}

}
