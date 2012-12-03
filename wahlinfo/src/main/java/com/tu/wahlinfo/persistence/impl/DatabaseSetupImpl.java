package com.tu.wahlinfo.persistence.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabaseSetup;

/**
 * @author Johannes
 * 
 */
@Stateless
public class DatabaseSetupImpl implements DatabaseSetup {

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseSetupImpl.class);

	private static final String INIT_SCRIPT_PATH = "/sql/init.sql";
	private static final boolean SETUP_ENABLED = false;

	@Inject
	DatabaseAccessor databaseAccessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tu.wahlinfo.persistence.DatabaseSetup#setup()
	 */
	@Override
	public void setup() throws DatabaseException {
		if (SETUP_ENABLED) {
			LOG.debug("Executing database setup");
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
							LOG.error("Could not execute " + statement, e);
						}
						statement = "";
					}
				}
			} catch (IOException e) {
				LOG.error("IOException", e);
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						LOG.error("IOException", e);
					}
				}
			}
		} else {
			LOG.info("Didn't execute database setup because it is disabled");
		}
	}
}
