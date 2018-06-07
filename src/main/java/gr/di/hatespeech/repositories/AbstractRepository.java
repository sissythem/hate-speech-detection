package gr.di.hatespeech.repositories;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * This is a base repository implementing Repository interface
 * Provides implementation in basic calls to the database
 * @author sissy
 * @param <T>
 */
public abstract class AbstractRepository<T> implements Repository<T> {
	protected static final String PERSISTENCE_UNIT_NAME = "hate-speech-detection";
	protected static EntityManagerFactory factory;
	protected String tableName;
	
	public AbstractRepository(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public void add(EntityManager em, T item) {
		if (item != null) {
			em.persist(item);			
		}
	}

	@Override
	public void add(EntityManager em, Iterable<T> items) {
		for (T item : items) {
			if (item != null)
				add(em, item);
		}
	}

	@Override
	public void update(EntityManager em, T item) {
		if (item != null)
			em.merge(item);
	}
	
	public void update(EntityManager em, Iterable<T> items) {
		for(T item : items) {
			if(item!=null) {
				em.merge(item);
			}
		}
	}

	@Override
	public void remove(EntityManager em, T item) {
		if (item != null) {
			item = em.merge(item);
			em.remove(item);
		}
	}
	
	@Override
	public void remove(EntityManager em, Iterable<T> items) {
		for(T item : items) {
			if(item!=null) {
				item = em.merge(item);
				em.remove(item);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> nativeQuery(EntityManager em, String query) {
		Query nativeQuery;
		if (query != null) {
			nativeQuery = em.createNativeQuery(query, getType());
		} else {
			nativeQuery = em.createNativeQuery("select * from " + tableName, getType());
		}

		return nativeQuery.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> namedQuery(EntityManager em, String queryName, HashMap<String, Object> parameters) {
		if (queryName != null) {
			if (parameters == null)
				return em.createNamedQuery(queryName).getResultList();
			else {
				Query query = em.createNamedQuery(queryName);
				for (Entry<String, Object> parameter : parameters.entrySet()) {
					query.setParameter(parameter.getKey(), parameter.getValue());
				}
				return query.getResultList();
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> jpqlQuery(EntityManager em, String query) {
		Query jpqlQuery;
		if (query != null) {
			jpqlQuery = em.createQuery(query);
		} else {
			jpqlQuery = em.createQuery("select * from " + tableName, getType());
		}
		return jpqlQuery.getResultList();
	}

	@Override
	public Object getSingleResult(EntityManager em, String queryName, HashMap<String, Object> parameters) {
		if (queryName != null) {
			if (parameters == null)
				return em.createNamedQuery(queryName).getSingleResult();
			else {
				Query query = em.createNamedQuery(queryName);
				for (Entry<String, Object> parameter : parameters.entrySet()) {
					query.setParameter(parameter.getKey(), parameter.getValue());
				}
				return query.getSingleResult();
			}
		}
		return null;
	}

	@Override
	public void flushChanges(EntityManager em) {
		em.flush();
	}
	
	@SuppressWarnings("unchecked")
	private Class<T> getType() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		return (Class<T>) type.getActualTypeArguments()[0];
	}


}
