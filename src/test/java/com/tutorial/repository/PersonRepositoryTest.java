package com.tutorial.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutorial.entity.Person;

public class PersonRepositoryTest {
	private static final Logger log = LoggerFactory.getLogger(PersonRepositoryTest.class);
	private static PersonRepository repository;
	private static Server server;

	@BeforeClass
	public static void beforeClass() throws SQLException {
		log.info("Starting H2 server...");
		server = Server.createTcpServer().start();
		log.info("H2 server started");
		log.info("Initializing entity manager factory...");
		repository = new PersonRepositoryImpl();
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

		person = repository.create(person);
		log.info("Created person: {}", person);

		assertThat(person.getId(), is(3L));
	}

	@Test
	public void testUpdate() {
		Person person = repository.read(1L);
		person.setModifiedDate(LocalDateTime.now());
		person.setFirstName("Abeiku");

		person = repository.update(person);
		person = null;

		person = repository.read(1L);
		assertNotNull(person);
		assertThat(person.getFirstName(), is("Abeiku"));
	}

	@Test
	public void testRead() {
		Person person = repository.read(1L);

		assertNotNull(person);
		assertThat(person.getFirstName(), is("Julius"));
	}

	@Test
	public void testDelete() {
		Person person = repository.read(2L);

		assertNotNull(person);
		repository.delete(person);

		person = repository.read(2L);
		assertNull(person);
	}

	@AfterClass
	public static void afterClass() {
		log.info("Closing entity manager factory...");
		repository.close();
		log.info("Entity manager factory closed");
		log.info("Shutting down H2 server...");
		server.stop();
		log.info("H2 server has shut down");
	}
}
