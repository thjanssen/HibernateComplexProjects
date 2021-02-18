package org.thoughts.on.java.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFetchStrategies {

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
			log.info(author);
		}
		
        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void selectBooks() {
		log.info("... selectBooks ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
		for (Book book : books){
			log.info("Book: "+book);
			log.info("Instance of Publisher? " + (book.getPublisher() instanceof Publisher));
			log.info(book.getPublisher().getClass());
			log.info("Publisher: "+book.getPublisher().getName());
		}

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void selectBook() {
		log.info("... selectBooks ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
        Book book = em.find(Book.class, 1L);
		log.info("Book: "+book);
		log.info("Publisher: "+book.getPublisher().getName());

        em.getTransaction().commit();
        em.close();
	}
}
