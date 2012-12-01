package com.tu.wahlinfo.persistence.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.util.ListUtil;
import com.tu.wahlinfo.persistence.Database;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseConstants;
import com.tu.wahlinfo.persistence.DatabaseException;

@Stateless
public class DatabaseImpl implements Database {

	static final String POSTGRESQL_QUOTE = "'";
	static int MAX_BULK_INSERT_SIZE = 20000;

	@Inject
	DatabaseAccessor databaseAccessor;

	@Override
	public void insert(String tableName, Map<String, String> values)
			throws DatabaseException {
		Map<String, List<String>> valuesForBulk = new LinkedHashMap<String, List<String>>();
		for (Entry<String, String> value : values.entrySet()) {
			valuesForBulk.put(value.getKey(),
					Collections.singletonList(value.getValue()));
		}
		bulkInsert(tableName, valuesForBulk);
	}

	@Override
	public void bulkInsert(String tableName, Map<String, List<String>> values)
			throws DatabaseException {
		bulkInsertCheckInput(tableName, values);

		Map<String, List<List<String>>> splicedValues = spliceValues(values);

		List<String> columns = new ArrayList<String>(splicedValues.keySet());
		String lastColumn = ListUtil.getLast(columns);

		String insertPrefix = generateInsertStatementPrefix(tableName, columns);

		int numberOfBulks = getNumberOfBulks(splicedValues);
		for (int bulk = 0; bulk < numberOfBulks; bulk++) {
			StringBuffer bulkSql = new StringBuffer(insertPrefix);
			int bulkSize = getBulkSize(splicedValues, bulk);
			for (int row = 0; row < bulkSize; row++) {
				bulkSql.append("(");
				for (String column : columns) {
					String columnValue = splicedValues.get(column).get(bulk)
							.get(row);
					bulkSql.append(sanitise(columnValue));
					if (!column.equals(lastColumn)) {
						bulkSql.append(", ");
					}
				}
				bulkSql.append(")");
				if (row < bulkSize - 1) {
					bulkSql.append(", ");
				}
			}

			bulkSql.append(";");
			databaseAccessor.executeStatement(bulkSql.toString());
		}

	}

	String sanitise(String value) {
		if (value.equals(DatabaseConstants.NULL)
				|| value.equals(DatabaseConstants.DEFAULT)) {
			return value;
		} else {
			return String.format(
					"%s%s%s",
					POSTGRESQL_QUOTE,
					value.replaceAll(POSTGRESQL_QUOTE, POSTGRESQL_QUOTE
							+ POSTGRESQL_QUOTE), POSTGRESQL_QUOTE);
		}
	}

	void bulkInsertCheckInput(String tableName, Map<String, List<String>> values) {
		if (tableName == null) {
			throw new IllegalArgumentException("tableName is null");
		}

		if (values == null) {
			throw new IllegalArgumentException("values is null");
		}

		if (!values.isEmpty()) {
			int mustHaveSize = -1;
			for (List<String> valueList : values.values()) {
				if (mustHaveSize == -1) {
					mustHaveSize = valueList.size();
				} else if (valueList.size() != mustHaveSize) {
					throw new IllegalArgumentException(
							"values. all value lists must have the same size");
				}
			}
		}
	}

	Map<String, List<List<String>>> spliceValues(
			Map<String, List<String>> values) {
		Map<String, List<List<String>>> splicedValues = new LinkedHashMap<String, List<List<String>>>();
		for (Entry<String, List<String>> value : values.entrySet()) {
			splicedValues.put(value.getKey(),
					ListUtil.splice(value.getValue(), MAX_BULK_INSERT_SIZE));
		}
		return splicedValues;
	}

	String generateInsertStatementPrefix(String tableName, List<String> columns) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(tableName).append(" (")
				.append(generateCommaSeparated(columns)).append(") values ");
		return sql.toString();
	}

	String generateCommaSeparated(List<String> elements) {
		StringBuffer result = new StringBuffer();
		for (String element : elements) {
			result.append(element);
			if (!element.equals(ListUtil.getLast(elements))) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	int getBulkSize(Map<String, List<List<String>>> splicedValues, int bulk) {
		return splicedValues.values().iterator().next().get(bulk).size();
	}

	int getNumberOfBulks(Map<String, List<List<String>>> splicedValues) {
		int numberOfBulks = 0;
		if (!splicedValues.values().isEmpty()) {
			numberOfBulks = splicedValues.values().iterator().next().size();
		}
		return numberOfBulks;
	}

}
