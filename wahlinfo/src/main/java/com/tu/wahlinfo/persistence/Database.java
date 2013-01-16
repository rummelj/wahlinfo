package com.tu.wahlinfo.persistence;

import java.util.List;
import java.util.Map;

/**
 * Enables interaction with the database.
 * 
 * @author Johannes
 * 
 */
public interface Database {

	/**
	 * Inserts one row into the database. This operation also sanitises
	 * parameter values.
	 * 
	 * @param tableName
	 *            The table to save the new row.
	 * @param values
	 *            The values of the new row given as key value pairs having the
	 *            column name as key and the values of that column as value
	 *            bundled together in a map.
	 * @throws DatabaseException
	 *             If an error occurs when accessing the database.
	 * 
	 */
	void insert(String tableName, Map<String, String> values)
			throws DatabaseException;

	/**
	 * Inserts multiple rows at once using an appropriate and efficient way
	 * relative to the underlying database implementation.
	 * 
	 * @param tableName
	 *            The table to save the new rows.
	 * @param values
	 *            The values of the new rows given as key value pairs having the
	 *            column name as key and all values for these columns as a list
	 *            of values bundled together as a map. All lists must have the
	 *            same size.
	 * 
	 *            e.g.:
	 * 
	 *            { {"name", ["John", "Jim"]}, {"age", [56, 23]}, }
	 * 
	 *            would insert two rows, one (John, 56) and the other (Jim, 23)
	 * 
	 * @throws DatabaseException
	 *             If the the lists of values are of different sizes or if an
	 *             error occurs accessing the database.
	 */
	void bulkInsert(String tableName, Map<String, List<String>> values)
			throws DatabaseException;

	/**
	 * Retrieves a constant as stored in WIConstants.
	 * 
	 * @param key
	 *            The key used in WIConstants.
	 * @return _value column, if the key does not exist, null is returned.
	 * @throws DatabaseException
	 *             If there's an error connecting to the database.
	 */
	String getConstants(String key) throws DatabaseException;

	/**
	 * Cand be used to sanitise parameters in queries.
	 * 
	 * @param dirty
	 * @return <Quote><Sanitised Value><Quote>
	 */
	String sanitise(String dirty);
}
