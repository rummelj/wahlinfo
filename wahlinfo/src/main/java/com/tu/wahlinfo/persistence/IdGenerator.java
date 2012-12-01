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

	/**
	 * Increases the id count by the given number.
	 * 
	 * @param byNumber
	 * @throws DatabaseException
	 */
	void increaseId(long byNumber) throws DatabaseException;
}
