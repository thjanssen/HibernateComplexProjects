package org.thoughts.on.java.cache;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thoughts.on.java.model.Author;
import org.thoughts.on.java.model.Book;

public class TestCacheManagement {

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
	public void test1stLevelCacheDetach() {
		log.info("... test1stLevelCacheEviction ...");
		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Author a1 = em.find(Author.class, 1L);
		Author a2 = em.find(Author.class, 2L);
		Book b1 = em.find(Book.class, 1L);
		
		logCachedObjects(em, a1, a2, b1);
		
		log.info("Detach Author 1");
		em.detach(a1);
		logCachedObjects(em, a1, a2, b1);
		
		log.info("Clear all");
		em.clear();
		logCachedObjects(em, a1, a2, b1);
		
		em.getTransaction().commit();
        em.close();
	}
	
	private void logCachedObjects(EntityManager em, Author a1, Author a2, Book b1) {
		log.info("Cache contains Author 1? " + em.contains(a1));
		log.info("Cache contains Author 2? " + em.contains(a2));
		log.info("Cache contains Book 1? " + em.contains(b1));
	}

	@Test
	public void testUpdateAfterCacheDetach() {
		log.info("... testUpdateAfterCacheEviction ...");
		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Author a = em.find(Author.class, 1L);
				
		log.info("Cache contains Author 1? " + em.contains(a));
		
		a.setFirstName("changed");
		a.setLastName("can't remember");
		
		log.info("Before cache eviction");
		Author a1 = em.find(Author.class, 1L);
		log.info(a1);
		
		em.flush();
		
		log.info("Detach Author 1");
		em.detach(a);
		log.info("Cache contains Author 1? " + em.contains(a));
		
		log.info("After detach");
		a1 = em.find(Author.class, 1L);
		log.info(a1);
		
		em.getTransaction().commit();
        em.close();
	}
}
