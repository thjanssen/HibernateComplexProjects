package org.thoughts.on.java.lazy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thoughts.on.java.model.Author;
import org.thoughts.on.java.model.BookPublisherValue;

public class TestNativeQuery {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
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
	public void testAuthorEntityMapping() {
		log.info("... testAuthorEntityMapping ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Author a = (Author) em.createNativeQuery("SELECT a.id, a.version, a.firstName, a.lastName FROM Author a WHERE a.id = 1", Author.class).getSingleResult();
		log.info(a.getClass().getName() + " - " + a);

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testAuthorEntityBookCountMapping() {
		log.info("... testAuthorEntityBookCountMapping ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
        Query q = em.createNativeQuery("SELECT a.id, a.version, a.firstName, a.lastName, count(b.bookid) as bookCount FROM Author a JOIN BookAuthor b ON b.authorid = a.id WHERE a.id = :id GROUP BY a.id, a.firstName, a.lastName, a.version", "AuthorBookCount");
        q.setParameter("id", 1);
		Object[] o = (Object[]) q.getSingleResult();
		log.info(o[0] + " wrote " + o[1] + " book(s)");

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testBookPublisherMapping() {
		log.info("... testBookPublisherMapping ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		BookPublisherValue v = (BookPublisherValue) em.createNativeQuery("SELECT b.title, p.name FROM Book b JOIN Publisher p ON b.publisherid = p.id WHERE b.id = 1", "BookPublisher").getSingleResult();
		log.info(v.getClass().getName() + " - " + v.getPublisher() + " published " + v.getTitle());

        em.getTransaction().commit();
        em.close();
	}
}
