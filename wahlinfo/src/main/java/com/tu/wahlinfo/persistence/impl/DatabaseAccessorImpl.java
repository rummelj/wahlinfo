package com.tu.wahlinfo.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

@Stateless
public class DatabaseAccessorImpl implements DatabaseAccessor {

	private static Logger LOG = LoggerFactory
			.getLogger(DatabaseAccessorImpl.class);

	@PersistenceContext(unitName = "wahlinfo")
	EntityManager entityManager;

	@Asynchronous
	public void executeStatementAsync(String sql) throws DatabaseException {
		executeStatement(sql);
	}

	@Override
	public void executeStatement(String sql) throws DatabaseException {
		if (sql == null || sql.isEmpty() || sql.trim().length() == 0) {
			LOG.error("Could not execute sql {}", sql);
		} else {
			try {
				entityManager.createNativeQuery(sql).executeUpdate();
				LOG.debug("Executed {}", sql);
			} catch (Exception e) {
				throw new DatabaseException("Could not execute " + sql, e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> executeQuery(String query,
			String... columnNames) throws DatabaseException {
		LOG.debug("Executing query {}", query);

		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (String columnName : columnNames) {
			result.put(columnName, new ArrayList<String>(8));
		}

		// Check uniqueness
		if (result.size() < columnNames.length) {
			throw new DatabaseException("Column names are not unique!");
		}

		List<Object> rows = entityManager.createNativeQuery(query)
				.getResultList();
		for (Object row : rows) {
			if (row instanceof Object[]) {
				Object[] rowArray = (Object[]) row;
				if (rowArray.length != columnNames.length) {
					throw new DatabaseException(
							"Not enough columns specified. Expected <"
									+ rowArray.length + ">, actual <"
									+ columnNames.length + ">");
				}
				for (int i = 0; i < rowArray.length; i++) {
					result.get(columnNames[i]).add(rowArray[i].toString());
				}
			} else {
				result.get(columnNames[0]).add(row.toString());
			}
		}
		return result;
	}
}
