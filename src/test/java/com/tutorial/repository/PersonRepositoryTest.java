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
package com.tutorial.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tutorial.entity.Person;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PersonRepositoryTest {
	private static final Logger log = LoggerFactory.getLogger(PersonRepositoryTest.class);
	@Inject
	private PersonRepository repository;

	@Before
	public void before() {
		Person person = new Person();
		person.setFirstName("Julius");
		person.setLastName("Krah");
		person.setCreatedDate(LocalDateTime.now());
		person.setDateOfBirth(LocalDate.of(1990, Month.APRIL, 4));
		
		repository.save(person);
	}

	@Test
	public void testCreate() {
		Person person = new Person();
		person.setFirstName("Loretta");
		person.setLastName("Krah");
		person.setCreatedDate(LocalDateTime.now());
		person.setDateOfBirth(LocalDate.of(1992, Month.AUGUST, 12));

		Optional<Person> p = repository.save(person);
		p.ifPresent(consumer -> {
			log.info("Created person: {}", consumer);
			assertThat(consumer.getId(), is(4L));
		});

	}

	@Test
	public void testUpdate() {
		Optional<Person> person = repository.findOne(6L);

		person.ifPresent(consumer -> {
			consumer.setModifiedDate(LocalDateTime.now());
			consumer.setFirstName("Abeiku");
			repository.save(consumer);
		});

		person = repository.findOne(6L);
		assertTrue(person.isPresent());

		assertThat(person.get().getFirstName(), is("Abeiku"));

	}

	@Test
	public void testRead() {
		Optional<Person> person = repository.findOne(2L);

		assertTrue(person.isPresent());
		assertThat(person.get().getFirstName(), is("Julius"));
	}

	@Test
	public void testDelete() {
		Optional<Person> person = repository.findOne(5L);
		assertTrue("Person does not exist", person.isPresent());

		person.ifPresent(consumer -> {
			assertNotNull(consumer);
			repository.delete(consumer);
		});

		person = repository.findOne(5L);
		assertFalse(person.isPresent());
	}

}
