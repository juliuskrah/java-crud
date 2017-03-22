/*
 * Copyright 2017, Julius Krah                                                 
 * by the @authors tag. See the LICENCE in the distribution for a              
 * full listing of individual contributors.                                   
 *                                                                           
 * Licensed under the Apache License, Version 2.0 (the "License");             
 * you may not use this file except in compliance with the License.            
 * You may obtain a copy of the License at                                     
 * http://www.apache.org/licenses/LICENSE-2.0                                  
 * Unless required by applicable law or agreed to in writing, software         
 * distributed under the License is distributed on an "AS IS" BASIS,           
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    
 * See the License for the specific language governing permissions and         
 * limitations under the License. 
 */
package com.tutorial;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.hikaricp.internal.HikariConfigurationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import com.tutorial.entity.Person;
import com.tutorial.repository.PersonRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	@Inject
	private Environment env;
	@Inject
	private ResourceLoader resourceLoader;

	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class);
		PersonRepository repository = ctx.getBean(PersonRepository.class);

		Person person = new Person();
		person.setFirstName("Julius");
		person.setLastName("Krah");
		person.setCreatedDate(LocalDateTime.now());
		person.setDateOfBirth(LocalDate.of(1990, Month.APRIL, 4));

		// Create person
		repository.save(person);

		// Hibernate generates id of 1
		Optional<Person> p = repository.findOne(1L);

		p.ifPresent(consumer -> {
			log.info("Person from database: {}", consumer);
			consumer.setModifiedDate(LocalDateTime.now());
			consumer.setFirstName("Abeiku");
		});
		// Update person record
		repository.save(p.get());

		p = Optional.empty();

		// Read updated record
		p = repository.findOne(1L);
		p.ifPresent(consumer -> {
			log.info("Person updated: {}", consumer);
		});
		// Delete person
		repository.delete(p.get());

		p = Optional.empty();

		p = repository.findOne(1L);

		log.info("Does person exist: {}", p.isPresent());
		((AnnotationConfigApplicationContext) ctx).close();
	}

	private Properties properties() {
		Properties props = new Properties();
		props.setProperty("javax.persistence.provider",
				env.getRequiredProperty("spring.jpa.properties.javax.persistence.provider"));
		props.setProperty("javax.persistence.schema-generation.database.action",
				env.getRequiredProperty("spring.jpa.hibernate.ddl-auto"));
		props.setProperty("hibernate.hikari.dataSourceClassName",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.dataSourceClassName"));
		props.setProperty("hibernate.hikari.dataSource.url",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.dataSource.url"));
		props.setProperty("hibernate.hikari.dataSource.user",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.dataSource.user"));
		props.setProperty("hibernate.hikari.dataSource.password",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.dataSource.password"));
		props.setProperty("hibernate.hikari.minimumIdle",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.minimumIdle"));
		props.setProperty("hibernate.hikari.maximumPoolSize",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.maximumPoolSize"));
		props.setProperty("hibernate.hikari.idleTimeout",
				env.getRequiredProperty("spring.jpa.properties.hibernate.hikari.idleTimeout"));
		props.setProperty("hibernate.connection.handling_mode",
				env.getRequiredProperty("spring.jpa.properties.hibernate.connection.handling_mode"));
		props.setProperty("hibernate.connection.provider_class",
				env.getRequiredProperty("spring.jpa.properties.hibernate.connection.provider_class"));

		return props;
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		log.debug("Starting datasource driver...");
		HikariConfig config = HikariConfigurationUtil.loadConfiguration(properties());

		return new HikariDataSource(config);
	}

	@Bean
	public SpringLiquibase liquibase(DataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(env.getRequiredProperty("liquibase.change-log"));
		liquibase.setContexts(env.getRequiredProperty("liquibase.contexts"));
		liquibase.setDropFirst(env.getRequiredProperty("liquibase.drop-first", Boolean.class));
		liquibase.setShouldRun(env.getRequiredProperty("liquibase.enabled", Boolean.class));
		log.debug("Configuring Liquibase...");

		return liquibase;
	}

	@PostConstruct
	public void checkChangelogExists() {
		if (env.getRequiredProperty("liquibase.check-change-log-location", Boolean.class)) {

			Resource resource = this.resourceLoader

					.getResource(env.getRequiredProperty("liquibase.change-log"));

			Assert.state(resource.exists(), "Cannot find changelog location: " + resource
					+ " (please add changelog or check your Liquibase configuration)");
		}

	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManager() {
		log.debug("Starting EntityManager...");
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setPackagesToScan("com.tutorial.entity");
		entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManager.setJpaProperties(properties());
		entityManager.setPersistenceUnitName("com.juliuskrah.tutorial");

		return entityManager;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		log.debug("Starting TransactionManager...");

		return new JpaTransactionManager(emf);
	}
}
