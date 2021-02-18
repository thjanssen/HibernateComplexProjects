package org.thoughts.on.java.cache;

import javax.persistence.Cache;
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
	public void test2ndLevelCacheEviction() {
		log.info("... test2ndLevelCacheEviction ...");
		
		log.info("populate cache");
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		em.find(Author.class, 1L);
		em.find(Author.class, 2L);
		em.find(Book.class, 1L);

        em.getTransaction().commit();
        em.close();
	
		// 2nd session
		em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Cache cache = em.getEntityManagerFactory().getCache();
		logCachedObjects(cache);
		
		log.info("Evict Author 1");
		cache.evict(Author.class, 1L);
		logCachedObjects(cache);
		
		log.info("Evict all authors");
		cache.evict(Author.class);
		logCachedObjects(cache);
		
		log.info("Evict all");
		cache.evictAll();
		logCachedObjects(cache);

        em.getTransaction().commit();
        em.close();
	}
	
	private void logCachedObjects(Cache cache) {
		log.info("Cache contains Author 1? " + cache.contains(Author.class, 1L));
		log.info("Cache contains Author 2? " + cache.contains(Author.class, 2L));
		log.info("Cache contains Book 1? " + cache.contains(Book.class, 1L));
	}
}
