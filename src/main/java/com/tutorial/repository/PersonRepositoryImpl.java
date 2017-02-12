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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.tutorial.entity.Person;

public class PersonRepositoryImpl implements PersonRepository {
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.juliuskrah.tutorial");
	private EntityManager em;

	public PersonRepositoryImpl() {
		em = emf.createEntityManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person create(Person person) {
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person read(Long id) {
		em.getTransaction().begin();
		Person person = em.find(Person.class, id);
		em.getTransaction().commit();
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Person update(Person person) {
		em.getTransaction().begin();
		person = em.merge(person);
		em.getTransaction().commit();
		return person;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person person) {
		em.getTransaction().begin();
		em.remove(person);
		em.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		emf.close();
	}
}
