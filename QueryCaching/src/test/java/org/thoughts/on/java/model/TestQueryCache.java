package org.thoughts.on.java.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestQueryCache {

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
	public void testQueryCacheAdHocQuery() {
		log.info("... testQueryCacheAdHocQuery ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Session s = (Session) em.getDelegate();
		Query q = s.createQuery("SELECT a FROM Author a WHERE id = :id");
		q.setParameter("id", 1L);
		q.setCacheable(true);
		log.info(q.uniqueResult());
		
		log.info(q.uniqueResult());

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testQueryCacheWithTwoTransactions() {
		log.info("... testQueryCache2transactions ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Session s = (Session) em.getDelegate();
		Query q = s.createQuery("SELECT a FROM Author a WHERE id = :id");
		q.setParameter("id", 1L);
		q.setCacheable(true);
		log.info(q.list().get(0));

        em.getTransaction().commit();
        em.close();
        
        em = emf.createEntityManager();
        em.getTransaction().begin();
        
		s = (Session) em.getDelegate();
		q = s.createQuery("SELECT a FROM Author a WHERE id = :id");
		q.setParameter("id", 1L);
		q.setCacheable(true);
		log.info(q.list().get(0));

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testQueryCacheUpdateEntity() {
		log.info("... testQueryCacheUpdateEntity ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Session s = (Session) em.getDelegate();
		Query q = s.createQuery("SELECT a FROM Author a WHERE id = :id");
		q.setParameter("id", 1L);
		q.setCacheable(true);
		Author a = (Author)q.list().get(0);
		
		a.setFirstName("changed");
		
		log.info(a);
		
		log.info(q.list().get(0));

        em.getTransaction().commit();
        em.close();
	}
	
	@Test
	public void testQueryCacheWithDifferentParameter() {
		log.info("... testQueryCacheWithDifferentParameter ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		Session s = (Session) em.getDelegate();
		Query q = s.createQuery("SELECT a FROM Author a WHERE id = :id");
		q.setParameter("id", 1L);
		q.setCacheable(true);
		log.info(q.list().get(0));
		
		q = s.createQuery("SELECT a FROM Author a WHERE id = :id");
		q.setParameter("id", 2L);
		q.setCacheable(true);
		log.info(q.list().get(0));

        em.getTransaction().commit();
        em.close();
	}
}
