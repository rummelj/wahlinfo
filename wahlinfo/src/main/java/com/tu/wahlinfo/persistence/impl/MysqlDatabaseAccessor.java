package com.tu.wahlinfo.persistence.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

@Stateless
public class MysqlDatabaseAccessor implements DatabaseAccessor {

	@PersistenceContext(unitName = "wahlinfo")
	EntityManager entityManager;

	@Override
	public void executeStatement(String sql) throws DatabaseException {
		if (sql == null || sql.isEmpty() || sql.trim().length() == 0) {
			// TODO: Log
		} else {
			try {
				entityManager.createNativeQuery(sql).executeUpdate();
			} catch (Exception e) {
				throw new DatabaseException("Could not execute " + sql, e);
			}
		}
	}

}
