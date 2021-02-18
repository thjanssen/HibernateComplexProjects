package org.thoughts.on.java.cache;

import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thoughts.on.java.model.Author;

public class TestSecondLevelCache {

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
	public void test2TX() {
		log.info("... test2TX ...");
		
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Author a1 = em.find(Author.class, 1L);
		log.info(a1);
		
		em.getTransaction().commit();
        em.close();
        
        em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Author a2 = em.find(Author.class, 1L);
		log.info(a2);

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testRelationshipCaching() {
		log.info("... testRelationshipCaching ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		// get author and his books
		Author a = em.find(Author.class, 1L);
		writeMessage(a);

        em.getTransaction().commit();
        em.close();
	
        // 2nd session
		em = emf.createEntityManager();
        em.getTransaction().begin();
        
		// get author and his books
		a = em.find(Author.class, 1L);
		writeMessage(a);
		

        em.getTransaction().commit();
        em.close();
	}
	
	private void writeMessage(Author a) {
		log.info("Author " + a.getFirstName() + " " + a.getLastName() + " wrote "
			+ a.getBooks().stream().map(b -> b.getTitle()).collect(Collectors.joining(", ")));
	}
}
