package com.tu.wahlinfo.persistence;

/**
 * Provides unique ids.
 * 
 * @author Johannes
 * 
 */
public interface IdGenerator {

	/**
	 * Provides a unique id.
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	long getId() throws DatabaseException;

}
