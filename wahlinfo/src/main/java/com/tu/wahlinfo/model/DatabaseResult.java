package com.tu.wahlinfo.model;

import java.util.Map;

/**
 * Represents a result coming from the database after a query as a table.
 * 
 * @author Johannes
 * 
 */
public interface DatabaseResult extends Iterable<Map<String, String>> {

}
