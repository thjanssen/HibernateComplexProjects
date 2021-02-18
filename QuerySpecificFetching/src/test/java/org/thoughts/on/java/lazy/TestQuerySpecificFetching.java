package org.thoughts.on.java.lazy;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.annotations.QueryHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thoughts.on.java.model.Author;
import org.thoughts.on.java.model.Author_;
import org.thoughts.on.java.model.Book_;

public class TestQuerySpecificFetching {

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
	public void testAnnotatedRelation() {
		log.info("... annotatedRelation ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		List<Author> authors = em.createQuery("SELECT a FROM Author a",
				Author.class).getResultList();

		writeMessage(authors);

        em.getTransaction().commit();
        em.close();
	}

	@Test
	public void testFetchJoinJPQL() {
		log.info("... fetchJoinJPQL ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		// Requires additional DISTINCT
        TypedQuery<Author>q = em.createQuery("SELECT DISTINCT a FROM Author a JOIN FETCH a.books b", Author.class);
        q.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);
		List<Author> authors = q.getResultList();

		writeMessage(authors);

        em.getTransaction().commit();
        em.close();
	}

	@Test
	public void testFetchJoinCriteria() {
		log.info("... fetchJoinCriteria ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		// Requires additional DISTINCT
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Author> q = cb.createQuery(Author.class);
		Root<Author> author = q.from(Author.class);
		author.fetch(Author_.books, JoinType.INNER);
		q.select(author);
		q.distinct(true);

		List<Author> authors = em.createQuery(q)
                                 .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                                 .getResultList();

		writeMessage(authors);

        em.getTransaction().commit();
        em.close();
	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testNamedFetchGraphAuthorBooks() {
		log.info("... namedFetchGraphAuthorBooks ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		EntityGraph graph = em.getEntityGraph("graph.AuthorBooks");

		List<Author> authors = em
				.createQuery("SELECT a FROM Author a", Author.class)
				.setHint("javax.persistence.fetchgraph", graph)
                .getResultList();

		writeMessage(authors);

        em.getTransaction().commit();
        em.close();
	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testNamedFetchGraphAuthorBooksReviews() {
		log.info("... namedFetchGraphAuthorBooksReviews ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		EntityGraph graph = em.getEntityGraph("graph.AuthorBooksReviews");

		List<Author> authors = em
				.createQuery("SELECT a FROM Author a", Author.class)
				.setHint("javax.persistence.fetchgraph", graph)
                .getResultList();

		writeMessageWithReviews(authors);

        em.getTransaction().commit();
        em.close();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testNamedLoadGraphAuthorBooksReviews() {
		log.info("... namedLoadGraphAuthorBooksReviews ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		EntityGraph graph = em.getEntityGraph("graph.AuthorBooksReviews");

		List<Author> authors = em
				.createQuery("SELECT a FROM Author a", Author.class)
				.setHint("javax.persistence.loadgraph", graph)
                .getResultList();

		writeMessageWithReviews(authors);

        em.getTransaction().commit();
        em.close();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void fetchGraphAuthorBooksReviews() {
		log.info("... fetchGraphAuthorBooksReviews ...");

		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
		EntityGraph graph = em.createEntityGraph(Author.class);
		Subgraph bookSubGraph = graph.addSubgraph(Author_.books);
		bookSubGraph.addSubgraph(Book_.reviews);

		List<Author> authors = em
				.createQuery("SELECT a FROM Author a", Author.class)
				.setHint("javax.persistence.fetchgraph", graph)
                .getResultList();

		writeMessageWithReviews(authors);

        em.getTransaction().commit();
        em.close();
	}
	
	private void writeMessage(List<Author> authors) {
		for (Author a : authors) {
			log.info("Author " + a.getFirstName() + " " + a.getLastName() + " wrote "
				+ a.getBooks().stream().map(b -> b.getTitle()).collect(Collectors.joining(", ")));
		}
	}
	
	private void writeMessageWithReviews(List<Author> authors) {
		for (Author a : authors) {
			log.info("Author " + a.getFirstName() + " " + a.getLastName() + " wrote "
				+ a.getBooks().stream().map(b -> b.getTitle() + "(" + b.getReviews().size() + " reviews)").collect(Collectors.joining(", ")));
		}
	}
}
