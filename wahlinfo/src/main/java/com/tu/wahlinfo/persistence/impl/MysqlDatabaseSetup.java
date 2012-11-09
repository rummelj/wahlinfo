package com.tu.wahlinfo.persistence.impl;

import java.util.Scanner;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabaseSetup;

/**
 * @author Johannes
 * 
 */
@Stateless
public class MysqlDatabaseSetup implements DatabaseSetup {

	private static final String INIT_SCRIPT_PATH = "sql/mysql_init.sql";

	@Inject
	DatabaseAccessor databaseAccessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tu.wahlinfo.persistence.DatabaseSetup#setup()
	 */
	@Override
	public void setup() throws DatabaseException {
		Scanner scanner = null;
		try {
			scanner = new Scanner(getClass().getResourceAsStream(
					INIT_SCRIPT_PATH)).useDelimiter("\\A");
			if (scanner.hasNext()) {
				databaseAccessor.executeStatement(scanner.next());
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

}
