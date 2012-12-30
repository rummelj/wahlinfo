package com.tu.wahlinfo.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a result coming from the database after a query as a table.
 * 
 * @author Johannes
 * 
 */
public interface DatabaseResult extends Iterable<Map<String, String>> {

	/**
	 * This only works if the query returns a result with only one column.
	 * 
	 * @return
	 */
	List<String> toList();
}
