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
package com.tutorial.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import org.hibernate.internal.SessionImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutorial.entity.Person;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class PersonRepositoryTest {
	private static final Logger log = LoggerFactory.getLogger(PersonRepositoryTest.class);
	private static PersonRepository repository;

	@BeforeClass
	public static void beforeClass() throws SQLException {
		log.info("Initializing entity manager factory...");
		repository = new PersonRepositoryImpl();
		Connection connection = repository.getEntityManager().unwrap(SessionImpl.class).connection();
		Database database = null;
		Liquibase liquibase = null;

		try {
			log.debug("Starting liquibase migration...");
			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			liquibase = new Liquibase("dbChangelog.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update("test");
		} catch (LiquibaseException e) {
			log.error("Error occured in execution: {}", e.getMessage());
			e.printStackTrace();
		}
		log.info("Entity manager factory started");
	}

	@Before
	public void before() {
		Person person = new Person();
		person.setFirstName("Julius");
		person.setLastName("Krah");
		person.setCreatedDate(LocalDateTime.now());
		person.setDateOfBirth(LocalDate.of(1990, Month.APRIL, 4));

		repository.create(person);
	}

	@Test
	public void testCreate() {
		Person person = new Person();
		person.setFirstName("Loretta");
		person.setLastName("Krah");
		person.setCreatedDate(LocalDateTime.now());
		person.setDateOfBirth(LocalDate.of(1992, Month.AUGUST, 12));

		Optional<Person> p = repository.create(person);
		p.ifPresent(consumer -> {
			log.info("Created person: {}", consumer);
			assertThat(consumer.getId(), is(3L));
		});

	}

	@Test
	public void testUpdate() {
		Optional<Person> person = repository.read(1L);
		person.ifPresent(consumer -> {
			consumer.setModifiedDate(LocalDateTime.now());
			consumer.setFirstName("Abeiku");
			repository.update(consumer);
		});

		person = repository.read(1L);
		assertTrue(person.isPresent());

		assertThat(person.get().getFirstName(), is("Abeiku"));

	}

	@Test
	public void testRead() {
		Optional<Person> person = repository.read(1L);

		assertTrue(person.isPresent());
		assertThat(person.get().getFirstName(), is("Julius"));
	}

	@Test
	public void testDelete() {
		Optional<Person> person = repository.read(2L);

		person.ifPresent(consumer -> {
			assertNotNull(consumer);
			repository.delete(consumer);
		});

		person = repository.read(2L);
		assertFalse(person.isPresent());
	}

	@AfterClass
	public static void afterClass() {
		log.info("Closing entity manager factory...");
		repository.close();
		log.info("Entity manager factory closed");
	}
}
