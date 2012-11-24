package com.tu.wahlinfo.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.model.Persistable;
import com.tu.wahlinfo.persistence.Database;
import com.tu.wahlinfo.persistence.DatabaseConstants;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.IdGenerator;

/**
 * @author Johannes
 * 
 */
@Stateless
public class DatabasePersisterImpl implements DatabasePersister {

	private static final String WI_DIRECT_CANDIDATE_TABLE_NAME = "WIDirectCandidate";
	private static final String WI_FEDERAL_STATE_TABLE_NAME = "WIFederalState";
	private static final String WI_ELECTORAL_DISTRICT_TABLE_NAME = "WIElectoralDistrict";
	private static final String WI_PARTY_TABLE_NAME = "WIParty";
	private static final String WI_ELECTION_TABLE_NAME = "WIElection";
	private static final String WI_PARTY_VOTES_TABLE_NAME = "WIPartyVotes";
	private static final String WI_LIST_CANDIDATE_TABLE_NAME = "WIListCandidate";
	private static final String WI_FILLED_VOTING_PAPER_TABLE_NAME = "WIFilledVotingPaper";

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
	public Long persist(Persistable persistable) throws DatabaseException {
		return persist(Collections.singleton(persistable)).get(persistable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tu.wahlinfo.persistence.DatabasePersister#persist(Collection<com.
	 * tu.wahlinfo .model.Persistable>)
	 */
	@Override
	public Map<Persistable, Long> persist(Collection<Persistable> persistables)
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

		Map<Persistable, Long> result = new HashMap<Persistable, Long>();
		for (Collection<Persistable> persistablesOfOneTargetTable : persistablesSortedAfterTargetTable
				.values()) {
			Map<Persistable, Long> idsOfOneTable = persistToOneTable(persistablesOfOneTargetTable);
			result.putAll(idsOfOneTable);
		}
		return result;
	}

	/**
	 * Requires all persistable to have the same target table name.
	 * 
	 * @return
	 * 
	 * @throws DatabaseException
	 */
	Map<Persistable, Long> persistToOneTable(
			Collection<Persistable> persistables) throws DatabaseException {
		Map<Persistable, Long> result = new HashMap<Persistable, Long>();
		if (!persistables.isEmpty()) {
			Map<String, List<String>> bulkInserValues = new HashMap<String, List<String>>();
			for (Persistable persistable : persistables) {
				Map<String, String> values = convertToValues(persistable);
				result.put(persistable,
						getKey(persistable.getTargetTableName(), values));
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
		return result;
	}

	Long getKey(String targetTableName, Map<String, String> entityValues) {
		String keyString = null;
		switch (targetTableName) {
		case WI_FEDERAL_STATE_TABLE_NAME:
			keyString = entityValues.get("federalStateId");
			break;
		case WI_ELECTORAL_DISTRICT_TABLE_NAME:
			keyString = entityValues.get("number");
			break;
		default:
			if (entityValues.containsKey("id")) {
				keyString = entityValues.get("id");
			}
		}
		return keyString == null ? null : Long.valueOf(keyString);
	}

	Map<String, String> convertToValues(Persistable persistable)
			throws DatabaseException {
		String tableName = persistable.getTargetTableName();
		switch (tableName) {
		case WI_FEDERAL_STATE_TABLE_NAME:
			return convertFederalState(persistable.toRelationalStruct());
		case WI_ELECTORAL_DISTRICT_TABLE_NAME:
			return convertElectoralDistrict(persistable.toRelationalStruct());
		case WI_DIRECT_CANDIDATE_TABLE_NAME:
			return convertDirectCandidate(persistable.toRelationalStruct());
		case WI_PARTY_TABLE_NAME:
			return convertParty(persistable.toRelationalStruct());
		case WI_ELECTION_TABLE_NAME:
			return convertElection(persistable.toRelationalStruct());
		case WI_FILLED_VOTING_PAPER_TABLE_NAME:
			return convertFilledVotingPaper(persistable.toRelationalStruct());
		case WI_LIST_CANDIDATE_TABLE_NAME:
			return convertListCandidate(persistable.toRelationalStruct());
		case WI_PARTY_VOTES_TABLE_NAME:
			return convertPartyVotes(persistable.toRelationalStruct());
		default:
			return persistable.toRelationalStruct();
		}
	}

	Map<String, String> convertElection(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "electionYear");
		checkRequired(values, "electionYear");
		return values;
	}

	Map<String, String> convertFilledVotingPaper(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "id", "electoralDistrictId", "partyId",
				"directCandidateId");
		replaceIfNotContained(values, "id", String.valueOf(idGenerator.getId()));
		checkRequired(values, "electoralDistrictId", "partyId",
				"directCandidateId");
		return values;
	}

	Map<String, String> convertListCandidate(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "id", "name", "profession", "rank",
				"partyId", "federalStateId", "electionYear");
		replaceIfNotContained(values, "id", String.valueOf(idGenerator.getId()));
		checkRequired(values, "rank", "partyId", "federalStateId",
				"electionYear");
		values = fillUpMissing(values, "name", "profession");
		return values;
	}

	Map<String, String> convertPartyVotes(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "federalStateId", "partyId",
				"receivedVotes");
		checkRequired(values, "federalStateId", "partyId");
		values = fillUpDefault(values, "receivedVotes");
		return values;
	}

	Map<String, String> convertParty(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "id", "name", "electionYear");
		replaceIfNotContained(values, "id", String.valueOf(idGenerator.getId()));
		checkRequired(values, "electionYear");
		values = fillUpMissing(values, "name");
		return values;
	}

	Map<String, String> convertFederalState(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "federalStateId", "name",
				"possibleVotes", "validVotes", "invalidVotes");
		replaceIfNotContained(values, "federalStateId",
				String.valueOf(idGenerator.getId()));
		replaceIfNotContained(values, "possibleVotes", "0");
		values = fillUpDefault(values, "validVotes", "invalidVotes");
		return values;
	}

	Map<String, String> convertElectoralDistrict(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "number", "name", "federalStateId",
				"possibleVotes", "validVotes", "invalidVotes");
		replaceIfNotContained(values, "number",
				String.valueOf(idGenerator.getId()));
		replaceIfNotContained(values, "possibleVotes", "0");
		values = fillUpDefault(values, "validVotes", "invalidVotes");
		return values;
	}

	Map<String, String> convertDirectCandidate(Map<String, String> values)
			throws DatabaseException {
		values = removeAllBut(values, "id", "name", "partyId",
				"electoralDistrictId", "electionYear", "receivedVotes");
		replaceIfNotContained(values, "id", String.valueOf(idGenerator.getId()));
		checkRequired(values, "partyId", "electoralDistrictId", "electionYear");
		fillUpDefault(values, "receivedVotes");
		values = fillUpMissing(values, "id", "name", "partyId",
				"electoralDistrictId", "electionYear");
		return values;
	}

	<T, O> void replaceIfNotContained(Map<T, O> input, T key, O defaultValue) {
		if (!input.containsKey(key)) {
			input.put(key, defaultValue);
		}
	}

	void checkRequired(Map<String, String> values, String... keys)
			throws DatabaseException {
		for (String key : keys) {
			if (!values.containsKey(key)) {
				throw new DatabaseException(key + " is missing!");
			}
		}
	}

	Map<String, String> fillUpDefault(Map<String, String> input, String... keys) {
		for (String key : keys) {
			if (!input.containsKey(key)) {
				input.put(key, DatabaseConstants.DEFAULT);
			}
		}
		return input;
	}

	Map<String, String> fillUpMissing(Map<String, String> input, String... keys) {
		for (String key : keys) {
			if (!input.containsKey(key)) {
				input.put(key, DatabaseConstants.NULL);
			}
		}
		return input;
	}

	@SuppressWarnings("unchecked")
	<P, Q> Map<P, Q> removeAllBut(Map<P, Q> input, P... keys) {
		List<P> keysAsList = Arrays.asList(keys);
		for (Iterator<P> keyIt = input.keySet().iterator(); keyIt.hasNext();) {
			P keyCurrent = keyIt.next();
			if (!keysAsList.contains(keyCurrent)) {
				keyIt.remove();
				// TODO: Log
			}
		}
		return input;
	}
}
