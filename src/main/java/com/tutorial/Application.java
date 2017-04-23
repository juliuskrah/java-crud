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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.tutorial.entity.Person;
import com.tutorial.repository.PersonRepository;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	@Inject
	private PersonRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner init() {
		return (args) -> {
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
		};
	}
}
