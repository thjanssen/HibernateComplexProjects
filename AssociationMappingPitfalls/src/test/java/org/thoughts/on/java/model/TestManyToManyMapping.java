package org.thoughts.on.java.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestManyToManyMapping {

	Logger log = Logger.getLogger(this.getClass().getName());

	private EntityManagerFactory emf;

	@Before
	public void init() {
		emf = Persistence.createEntityManagerFactory("my-persistence-unit");
	}

	@After
	public void close() {
		emf.close();
	}

	@Test
	public void testMapping() {
		log.info("... testMapping ...");

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		Book b = em.find(Book.class, 1L);
		Author a = b.getAuthors().iterator().next();
		b.getAuthors().remove(a);
		
		em.getTransaction().commit();
		em.close();
	}
	
	@Test
	public void manageAssociation() {
		log.info("... manageAssociation ...");

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		Book b = new Book();
		b.setId(100L);
		b.setTitle("Hibernate Tips - More than 70 solutions to common Hibernate problems");
		em.persist(b);
		
		Author a = em.find(Author.class, 1L);
		a.getBooks().add(b);
		// b.getAuthors().add(a);
		// a.addBook(b);
		
		Author author = em.createQuery("SELECT a FROM Author a JOIN FETCH a.books WHERE a.id = 1", Author.class).getSingleResult();
		log.info(author);
		
		em.getTransaction().commit();
		em.close();
	}
}
