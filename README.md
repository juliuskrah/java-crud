[ ![Codeship Status for juliuskrah/java-crud](https://app.codeship.com/projects/e1e1f9d0-d361-0134-0c11-16d407f7e953/status?branch=spring-data-hibernate-jpa)](https://app.codeship.com/projects/201898)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
# CRUD Operations with Spring, Hibernate and JPA using Liquibase Migrations
In this repository you will find code that performs basic CRUD operations on H2 database using Spring's abstraction. The source
code found here uses Container managed Entity Manager. The lifecycle of a transaction will be managed by the spring container.
This implies no need to call `begin` and `close` or `rollback` of the transaction class. The container will also close the 
`EntityManagerFactory` after application terminates.   
This repository also showcases how to perform basic CRUD using Spring.  

The tutorial accompanying this repository is located at [http://juliuskrah.com/tutorial/2017/03/22/crud-operations-with-spring-data-jpa/](http://juliuskrah.com/tutorial/2017/03/22/crud-operations-with-spring-data-jpa/)

## Prerequisites
- Maven
- JDK

# Setup
Just import into your IDE and run main method
