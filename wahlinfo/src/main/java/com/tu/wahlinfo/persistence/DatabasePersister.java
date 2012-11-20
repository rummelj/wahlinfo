package com.tu.wahlinfo.persistence;

import java.util.Collection;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

/**
 * Allows persisting entities that implement the interface Persistable.
 * 
 * @author Johannes
 * 
 */
public interface DatabasePersister {

	/**
	 * Persists the entity and tries to automatically set empty attribute
	 * values.
	 * 
	 * @param persistable
	 *            The entity to persist.
	 * @return The id the persisted entity received.
	 * @throws DatabaseException
	 *             If an attribute is missing and cannot be determined
	 *             automaticly or if there is another error regarding the
	 *             database.
	 */
	public Long persist(Persistable persistable) throws DatabaseException;

	/**
	 * Bulk persists entities and tries to automatically set empty attribute
	 * values.
	 * 
	 * @param persistables
	 *            The entities to persist.
	 * @return The ids the persisted entities received.
	 * @throws DatabaseException
	 *             If an attribute is missing and cannot be determined
	 *             automaticly or if there is another error regarding the
	 *             database. This method is not transactional - if an error
	 *             occurs after n entries have been processed, they will stay in
	 *             the database.
	 */
	public Map<Persistable, Long> persist(Collection<Persistable> persistables)
			throws DatabaseException;

}
