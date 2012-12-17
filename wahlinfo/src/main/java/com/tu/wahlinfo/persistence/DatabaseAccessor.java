package com.tu.wahlinfo.persistence;

import java.util.List;
import java.util.Map;

/**
 * Provides access to the database on a very low level.
 * 
 * @author Johannes
 * 
 */
public interface DatabaseAccessor {
	
	/**
	 * Performs a db vacuum and analyze operation
	 * @param tableNames In no tables are provided the whole database is processed.
	 */
	void vacuumAndAnalyze(String... tableNames) throws DatabaseException;

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

	/**
	 * Executes sql as is without waiting for the database to process the query.
	 * This method does not sanitise any parameters, so you have to do that
	 * yourself.
	 * 
	 * @param sql
	 *            Sql to be executed on the database.
	 * @throws DatabaseException
	 *             If an error occurs when accessing the database.
	 */
	void executeStatementAsync(String sql) throws DatabaseException;

	/**
	 * Executes a query on the database.
	 * 
	 * @param query
	 *            The query to execute in SQL 92.
	 * @param The
	 *            ordered and expected column names.
	 * @return A map containing the result. If you have two rows (John, 43) and
	 *         (Jim, 24) in a table with column names (name, age), there would
	 *         be a map containing two keys (name) and (age), having each two
	 *         lists as their values containing (John, Jim) and (43, 24).
	 * @throws DatabaseException
	 *             If an error occurs when accessing the database or if the
	 *             number of columns is wrong or the name of the columns is not
	 *             unique.
	 */
	Map<String, List<String>> executeQuery(String query, String... columnNames)
			throws DatabaseException;

}
