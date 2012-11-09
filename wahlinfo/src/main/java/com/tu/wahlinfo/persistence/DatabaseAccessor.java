package com.tu.wahlinfo.persistence;

/**
 * Provides access to the database on a very low level.
 * 
 * @author Johannes
 * 
 */
public interface DatabaseAccessor {

	/**
	 * Executes sql as is. This method does not sanitise any parameters, so you
	 * have to do that yourself.
	 * 
	 * @param sql
	 *            Sql to be executed on the database.
	 * @throws DatabaseException
	 *             If an error occurs when accessing the database.
	 */
	void executeStatement(String sql) throws DatabaseException;

}
