package org.thoughts.on.java.cache;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thoughts.on.java.model.Author;
import org.thoughts.on.java.model.AuthorValue;
import org.thoughts.on.java.model.Book;

public class TestFirstLevelCache {
	
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
	public void testEMFind() {
		log.info("... testEMFind ...");
		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
		
		for (int i = 0; i < 2; i++) {
			log.info("Iteration: "+i);
			
			Author a = em.find(Author.class, 1L);
			log.info(a);
			
			for (Book b : a.getBooks()) {
				log.info(b);
			}
		}
		
        em.getTransaction().commit();
        em.close();
	}
	

	@Test
	public void test2sessions() {
		log.info("... test2sessions ...");
		
		EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();
		Author a1 = em1.find(Author.class, 1L);
		log.info(a1);
		
		EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
		Author a2 = em2.find(Author.class, 1L);
		log.info(a2);
		
        em1.getTransaction().commit();
        em1.close();
        em2.getTransaction().commit();
        em2.close();
	}
	
	@Test
	public void testJPQLQuery() {
		log.info("... testJPQLQuery ...");
		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		for (int i = 0; i < 2; i++) {
			log.info("Iteration: "+i);
			
			Author a1 = em.createQuery("SELECT a FROM Author a WHERE id = ?1", Author.class).setParameter(1, 1L).getSingleResult();
			log.info(a1);
		}
		
        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testConstructorQuery() {
		log.info("... testConstructorQuery ...");
		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		for (int i = 0; i < 2; i++) {
			log.info("Iteration: "+i);
			
			AuthorValue a1 = em.createQuery("SELECT new org.thoughts.on.java.model.AuthorValue(a.id, a.version, a.firstName, a.lastName) FROM Author a WHERE id = ?1", AuthorValue.class).setParameter(1, 1L).getSingleResult();
			log.info(a1);
		}
		
        em.getTransaction().commit();
        em.close();
	}
}
