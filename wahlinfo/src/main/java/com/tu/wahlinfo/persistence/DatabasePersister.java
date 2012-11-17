package com.tu.wahlinfo.persistence;

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
	 * @throws DatabaseException
	 *             If an attribute is missing and cannot be determined
	 *             automaticly or if there is another error regarding the
	 *             database.
	 */
	public void persist(Persistable persistable) throws DatabaseException;

}
