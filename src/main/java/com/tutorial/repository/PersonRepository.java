package com.tutorial.repository;

import com.tutorial.entity.Person;

/**
 * 
 * @author Julius Krah
 *
 */
public interface PersonRepository {
	/**
	 * Create a new Person
	 * 
	 * @param person
	 * @return Person
	 */
	Person create(Person person);

	/**
	 * Read Person by id
	 * 
	 * @param id
	 * @return Person
	 */
	Person read(Long id);

	/**
	 * Update person
	 * 
	 * @param person
	 */
	Person update(Person person);

	/**
	 * Delete person
	 * 
	 * @param person
	 */
	void delete(Person person);

	/**
	 * close the entity manager factory
	 */
	void close();

}
