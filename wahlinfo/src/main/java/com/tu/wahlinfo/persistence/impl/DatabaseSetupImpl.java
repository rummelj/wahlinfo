package com.tu.wahlinfo.persistence.impl;

import com.tu.util.FileScanner;
import com.tu.wahlinfo.model.Persistable;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.DatabaseSetup;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Johannes
 * 
 */
@Stateless
public class DatabaseSetupImpl implements DatabaseSetup {

	private static final String DIVISOR_VALUES_CSV = "/csv/DivisorValues.csv";
	private static final String SQL_UPPER_DISTRIBUTION_VIEW = "/sql/UpperDistributionViewForElection.sql";
	private static final String SQL_LOWER_DISTRIBUTION_VIEW = "/sql/LowerDistributionViewForElection.sql";
	private static final String SQL_ELECTED_CANDIDATES_VIEWS = "/sql/ElectedCandidatesViewsForElection.sql";
	private static final String SQL_MOST_CONCISE_WINNERS_VIEW = "/sql/MostConciseWinnersViewForElection.sql";

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseSetupImpl.class);

	private static final String INIT_SCRIPT_PATH = "/sql/init.sql";
	private static final boolean SETUP_ENABLED = true;

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
			LOG.info("Executing database setup");
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
			createViews();
			LOG.info("Setup complete");
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

	private void createViews() {
		try {
			String upperDistributionView = FileScanner
					.scanFile(SQL_UPPER_DISTRIBUTION_VIEW);
			String lowerDistributionView = FileScanner
					.scanFile(SQL_LOWER_DISTRIBUTION_VIEW);
			String electedCandidatesViews = FileScanner
					.scanFile(SQL_ELECTED_CANDIDATES_VIEWS);
			String mostConciseWinnersView = FileScanner
					.scanFile(SQL_MOST_CONCISE_WINNERS_VIEW);
			LOG.debug("Creating upper distribution views");
			this.databaseAccessor.executeStatement(upperDistributionView
					.replace(":electionYear", "2005"));
			this.databaseAccessor.executeStatement(upperDistributionView
					.replace(":electionYear", "2009"));
			LOG.debug("Creating lower distribution views");
			this.databaseAccessor.executeStatement(lowerDistributionView
					.replace(":electionYear", "2005"));
			this.databaseAccessor.executeStatement(lowerDistributionView
					.replace(":electionYear", "2009"));
			LOG.debug("Creating elected candidates views");
			this.databaseAccessor.executeStatement(electedCandidatesViews
					.replace(":electionYear", "2005"));
			this.databaseAccessor.executeStatement(electedCandidatesViews
					.replace(":electionYear", "2009"));
			this.databaseAccessor.executeStatement(mostConciseWinnersView
					.replace(":electionYear", "2005"));
			this.databaseAccessor.executeStatement(mostConciseWinnersView
					.replace(":electionYear", "2009"));
			LOG.info("View setup completed.");
		} catch (IOException ex) {
			LOG.error("Could not read at least one view sql file", ex);
		} catch (DatabaseException ex) {
			LOG.error("Error executing sql view files", ex);
		}
	}

}
