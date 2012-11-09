package com.tu.wahlinfo.persistence;

/**
 * Sets up the database using init.sql in resources.
 * 
 * @author Johannes
 * 
 */
public interface DatabaseSetup {

	/**
	 * Executes an sql script that is defined in resources folder which
	 * initialises database tables (drop first, create then)
	 * 
	 * @throws DatabaseException
	 *             If an error occurs while connecting to the database
	 */
	void setup() throws DatabaseException;

}
