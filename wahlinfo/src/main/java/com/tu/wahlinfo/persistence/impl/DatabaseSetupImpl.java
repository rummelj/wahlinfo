package com.tu.wahlinfo.persistence.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.model.Persistable;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.DatabaseSetup;

/**
 * @author Johannes
 * 
 */
@Stateless
public class DatabaseSetupImpl implements DatabaseSetup {

	private static final String DIVISOR_VALUES_CSV = "/csv/DivisorValues.csv";

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseSetupImpl.class);

	private static final String INIT_SCRIPT_PATH = "/sql/init.sql";
	private static final boolean SETUP_ENABLED = false;

	@Inject
	DatabaseAccessor databaseAccessor;

	@Inject
	DatabasePersister databasePersister;

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

			insertDivisorValues();
		} else {
			LOG.info("Didn't execute database setup because it is disabled");
		}
	}

	private void insertDivisorValues() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(DIVISOR_VALUES_CSV)));
		try {
			while (reader.ready()) {
				final String line = reader.readLine();
				databasePersister.persist(new Persistable() {
					@Override
					public Map<String, String> toRelationalStruct() {
						Map<String, String> result = new HashMap<String, String>();
						result.put("id", line.split(",")[0]);
						result.put("value", line.split(",")[1]);
						return result;
					}

					@Override
					public String getTargetTableName() {
						return "WIDivisor";
					}
				});
				LOG.debug("Wrote divisor value {}", line);
			}
		} catch (IOException e) {
			LOG.error("Could not read divisor csv");
		} catch (DatabaseException e) {
			LOG.error("Error writing divisor entry");
		}
	}
}
