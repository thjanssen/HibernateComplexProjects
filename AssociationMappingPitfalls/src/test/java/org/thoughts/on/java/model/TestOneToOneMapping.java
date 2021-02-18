package org.thoughts.on.java.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestOneToOneMapping {

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
	public void unidirectionalOneToOne() {
		log.info("... unidirectionalOneToOne ...");

		// Persist Book and Manuscript
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Book b = new Book();
		b.setId(100L);
		b.setTitle("Hibernate Tips - More than 70 solutions to common Hibernate problems");
		em.persist(b);
		
		Manuscript m = new Manuscript();
		m.setBook(b);
		
		em.persist(m);
		
		em.getTransaction().commit();
		em.close();
		
		// Read Book and Manuscript
		em = emf.createEntityManager();
		em.getTransaction().begin();

		m = em.find(Manuscript.class, m.getId());
		Assert.assertEquals(b, m.getBook());
		
		b = em.find(Book.class,  b.getId());
		m = em.find(Manuscript.class, b.getId());
		
		em.getTransaction().commit();
		em.close();
	}
}
