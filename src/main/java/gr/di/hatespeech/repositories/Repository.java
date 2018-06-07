package gr.di.hatespeech.repositories;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * This is an interface representing a repository
 * and containing basic calls to the database
 * @author sissy
 * @param <T>
 */
public interface Repository<T> {
	void add(EntityManager em, T item);
	void add(EntityManager em, Iterable<T> items);
	void update(EntityManager em, T item);
	void remove(EntityManager em, T item);
	void remove(EntityManager em, Iterable<T> items);
	List<T> nativeQuery(EntityManager em, String query);
	List<T> namedQuery(EntityManager em, String queryName, HashMap<String,Object> parameters);
	List<T> jpqlQuery(EntityManager em, String query);
	Object getSingleResult(EntityManager em, String queryName, HashMap<String, Object> parameters);
	void flushChanges(EntityManager em);
}

