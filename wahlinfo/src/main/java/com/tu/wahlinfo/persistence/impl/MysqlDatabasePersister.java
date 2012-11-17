package com.tu.wahlinfo.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.model.Persistable;
import com.tu.wahlinfo.persistence.Database;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.IdGenerator;

/**
 * @author Johannes
 * 
 */
@Stateless
public class MysqlDatabasePersister implements DatabasePersister {

	private static final String WI_DIRECT_CANDIDATE_TABLE_NAME = "WIDirectCandidate";

	private static final String NULL = "null";

	@Inject
	Database database;

	@Inject
	IdGenerator idGenerator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tu.wahlinfo.persistence.DatabasePersister#persist(com.tu.wahlinfo
	 * .model.Persistable)
	 */
	@Override
	public void persist(Persistable persistable) throws DatabaseException {
		persist(Collections.singleton(persistable));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tu.wahlinfo.persistence.DatabasePersister#persist(Collection<com.
	 * tu.wahlinfo .model.Persistable>)
	 */
	@Override
	public void persist(Collection<Persistable> persistables)
			throws DatabaseException {
		Map<String, Collection<Persistable>> persistablesSortedAfterTargetTable = new HashMap<String, Collection<Persistable>>();
		for (Persistable persistable : persistables) {
			String targetTableName = persistable.getTargetTableName();
			if (!persistablesSortedAfterTargetTable
					.containsKey(targetTableName)) {
				persistablesSortedAfterTargetTable.put(targetTableName,
						new HashSet<Persistable>());
			}
			persistablesSortedAfterTargetTable.get(targetTableName).add(
					persistable);
		}

		for (Collection<Persistable> persistablesOfOneTargetTable : persistablesSortedAfterTargetTable
				.values()) {
			persistToOneTable(persistablesOfOneTargetTable);
		}
	}

	/**
	 * Requires all persistable to have the same target table name.
	 * 
	 * @throws DatabaseException
	 */
	void persistToOneTable(Collection<Persistable> persistables)
			throws DatabaseException {
		if (!persistables.isEmpty()) {
			Map<String, List<String>> bulkInserValues = new HashMap<String, List<String>>();
			for (Persistable persistable : persistables) {
				Map<String, String> values = convertToValues(persistable);
				for (Entry<String, String> value : values.entrySet()) {
					if (!bulkInserValues.containsKey(value.getKey())) {
						bulkInserValues.put(value.getKey(),
								new ArrayList<String>(persistables.size()));
					}
					bulkInserValues.get(value.getKey()).add(value.getValue());
				}
			}
			String tableName = persistables.iterator().next()
					.getTargetTableName();
			database.bulkInsert(tableName, bulkInserValues);
		}
	}

	Map<String, String> convertToValues(Persistable persistable)
			throws DatabaseException {
		String tableName = persistable.getTargetTableName();
		switch (tableName) {
		case WI_DIRECT_CANDIDATE_TABLE_NAME:
			return convertDirectCandidate(persistable.toRelationalStruct());
		default:
			return persistable.toRelationalStruct();
		}
	}

	Map<String, String> convertDirectCandidate(Map<String, String> values)
			throws DatabaseException {
		/*
		 * id INTEGER UNSIGNED NOT NULL, name VARCHAR(255), partyId INTEGER
		 * UNSIGNED NOT NULL, electoralDistrictId SMALLINT UNSIGNED NOT NULL,
		 * electionId INTEGER UNSIGNED NOT NULL,
		 */

		values = removeAllBut(values, "id", "name", "partyId",
				"electoralDistrictId", "electionId");

		if (!values.containsKey("id")) {
			values.put("id", String.valueOf(idGenerator.getId()));
		}

		checkRequired(values, "partyId", "electoralDistrictId", "electionId");

		values = fillUpMissing(values, "id", "name", "partyId",
				"electoralDistrictId", "electionId");

		return values;
	}

	void checkRequired(Map<String, String> values, String... keys)
			throws DatabaseException {
		for (String key : keys) {
			if (!values.containsKey(key)) {
				throw new DatabaseException(key + " is missing!");
			}
		}
	}

	Map<String, String> fillUpMissing(Map<String, String> input, String... keys) {
		for (String key : keys) {
			if (!input.containsKey(key)) {
				input.put(key, NULL);
			}
		}
		return input;
	}

	@SuppressWarnings("unchecked")
	<P, Q> Map<P, Q> removeAllBut(Map<P, Q> input, P... keys) {
		List<P> keysAsList = Arrays.asList(keys);
		for (P keyCurrent : input.keySet()) {
			if (!keysAsList.contains(keyCurrent)) {
				input.remove(keyCurrent);
				// TODO: Log
			}
		}
		return input;
	}
}
