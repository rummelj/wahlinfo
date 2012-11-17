package com.tu.wahlinfo.persistence.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

	private static final String INIT_SCRIPT_PATH = "/sql/mysql_init.sql";

	@Inject
	DatabaseAccessor databaseAccessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tu.wahlinfo.persistence.DatabaseSetup#setup()
	 */
	@Override
	public void setup() throws DatabaseException {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream(INIT_SCRIPT_PATH)));
			String line;
			String statement = "";
			while ((line = bufferedReader.readLine()) != null) {
				statement += line.trim() + " ";
				if (statement.contains(";")) {
					try {
						databaseAccessor.executeStatement(statement);
					} catch (DatabaseException e) {
						// TODO: Log
					}
					statement = "";
				}
			}
		} catch (IOException e) {
			// TODO: Log
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO: Log
				}
			}
		}
	}
}
