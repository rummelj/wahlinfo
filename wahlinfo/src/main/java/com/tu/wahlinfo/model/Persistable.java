package com.tu.wahlinfo.model;

import java.util.Map;

public interface Persistable {

	/**
	 * Is used to determine in which table this persistable should be inserted
	 * and which strategy should be used to determine missing attribute values.
	 * 
	 * @return
	 */
	public String getTargetTableName();

	/**
	 * Transforms the object's data to a relational struct. Member names thereby
	 * serve as map-key and their value as map-value.
	 * 
	 * @return The resulting map.
	 */
	public Map<String, String> toRelationalStruct();

}
