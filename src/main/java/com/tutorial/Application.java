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

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutorial.entity.Person;
import com.tutorial.repository.PersonRepositoryImpl;

public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		Server server = null;
		PersonRepositoryImpl repository = null;
		try {
			// Start H2 embedded database
			server = Server.createTcpServer().start();
			log.info("Server started");

			Person person = new Person();
			person.setFirstName("Julius");
			person.setLastName("Krah");
			person.setCreatedDate(LocalDateTime.now());
			person.setDateOfBirth(LocalDate.of(1990, Month.APRIL, 4));

			repository = new PersonRepositoryImpl();
			// Create person
			repository.create(person);

			person = null;
			// Hibernate generates id of 1
			person = repository.read(1L);

			log.info("Person from database: {}", person);

			person.setModifiedDate(LocalDateTime.now());
			person.setFirstName("Abeiku");
			// Update person record
			repository.update(person);

			person = null;
			// Read updated record
			person = repository.read(1L);

			log.info("Person updated: {}", person);
			// Delete person
			repository.delete(person);
			
			person = repository.read(1L);

			log.info("Person deleted: {}", person);

		} catch (SQLException e) {
			log.error("Error occurred in initialization: " + e.getMessage());
			e.printStackTrace();
		} finally {
			log.info("Server shutting down");
			if (server != null)
				server.stop();
			log.info("Shutdown complete");
			log.info("Closing Entity Manager Factory");
			if (repository != null)
				repository.close();
			log.info("Entity Manager Factory closed ");
		}
	}

}
