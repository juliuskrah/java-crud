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

import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tutorial.entity.Person;

@Component
@Transactional
public class PersonRepositoryImpl implements PersonRepository {
	@PersistenceUnit(unitName = "com.juliuskrah.tutorial")
	private EntityManagerFactory emf;
	private EntityManager em;

	public PersonRepositoryImpl() {
	}

	@PostConstruct
	public void initEntityManager() {
		this.em = this.emf.createEntityManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Person> create(Person person) {
		Objects.requireNonNull(person, "Person must not be null");
		this.em.persist(person);
		return Optional.of(person);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	public Optional<Person> read(Long id) {
		Person person = this.em.find(Person.class, id);
		return Optional.ofNullable(person);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Person> update(Person person) {
		Objects.requireNonNull(person, "Person must not be null");
		person = this.em.merge(person);
		return Optional.of(person);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person person) {
		this.em.remove(person);
	}

}
