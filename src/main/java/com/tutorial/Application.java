/*
 * Copyright 2016, Julius Krah                                                 
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

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutorial.entity.Person;
import com.tutorial.repository.PersonRepositoryImpl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		PersonRepositoryImpl repository = null;
		try {
			repository = new PersonRepositoryImpl();

			init(repository.getEntityManager());

			Person person = new Person();
			person.setFirstName("Julius");
			person.setLastName("Krah");
			person.setCreatedDate(LocalDateTime.now());
			person.setDateOfBirth(LocalDate.of(1990, Month.APRIL, 4));

			// Create person
			repository.create(person);

			// Hibernate generates id of 1
			Optional<Person> p = repository.read(1L);

			p.ifPresent(consumer -> {
				log.info("Person from database: {}", consumer);
				consumer.setModifiedDate(LocalDateTime.now());
				consumer.setFirstName("Abeiku");
			});
			// Update person record
			repository.update(p.get());

			p = Optional.empty();

			// Read updated record
			p = repository.read(1L);
			p.ifPresent(consumer -> {
				log.info("Person updated: {}", consumer);
			});
			// Delete person
			repository.delete(p.get());

			p = Optional.empty();

			p = repository.read(1L);

			log.info("Does person exist: {}", p.isPresent());

		} finally {
			log.info("Closing Entity Manager Factory");
			if (repository != null)
				repository.close();
			log.info("Entity Manager Factory closed ");
		}
	}

	private static void init(EntityManager em) {

		Connection connection = em.unwrap(SessionImpl.class).connection();
		Database database = null;
		Liquibase liquibase = null;

		try {
			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			liquibase = new Liquibase("dbChangelog.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update("test");
		} catch (LiquibaseException e) {
			log.error("Error occured in execution: {}", e.getMessage());
			e.printStackTrace();
		}

	}
}
