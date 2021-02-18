package org.thoughts.on.java.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHibernateStatistics {
	
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
	public void selectAuthors() {
		log.info("... selectAuthors ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		List<Author> authors = em.createQuery("SELECT a FROM Author a",
				Author.class).getResultList();
		for (Author author : authors) {
			log.info(author + " has written " + author.getBooks().size() + " books.");
		}
		
        em.getTransaction().commit();
        em.close();
	}
}
