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
		entityManager.createNativeQuery(sql).executeUpdate();
	}

}
