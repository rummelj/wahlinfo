package com.tu.wahlinfo.analysis.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

@Stateless
public class VoteAnalysisImpl implements IVoteAnalysis {

	private static final String FILE_PATH_SQL_BASE = "/sql/";
	private static final String FILE_PATH_DIR_VOTE_AGGREGATION_SCRIPT = FILE_PATH_SQL_BASE
			+ "aggregateDirCanVotes.sql";
	private static final String FILE_PATH_LIST_VOTE_AGGREGATION_SCRIPT = FILE_PATH_SQL_BASE
			+ "aggregatePartyVotes.sql";
	private static final String FILE_PATH_QUERIES_BASE = FILE_PATH_SQL_BASE
			+ "queries/";
	private static final String FILE_PATH_PARTY_UPPER_DISTRIBUTION = FILE_PATH_QUERIES_BASE
			+ "UpperDistributionForElection.sql";
	private static final String FILE_PATH_LOWER_DISTRIBUTION = FILE_PATH_QUERIES_BASE
			+ "LowerDistributionForElection.sql";
	private static final Logger LOG = LoggerFactory
			.getLogger(VoteAnalysisImpl.class);

	@Inject
	DatabaseAccessor databaseAccessor;

	@Override
	public void updateVoteBase() throws DatabaseException, IOException {

		LOG.debug("Updating party votes");
		databaseAccessor.executeStatement(this
				.readSqlFile(FILE_PATH_LIST_VOTE_AGGREGATION_SCRIPT));
		LOG.debug("Done");

		LOG.debug("Updating direct candidate votes");
		databaseAccessor.executeStatement(this
				.readSqlFile(FILE_PATH_DIR_VOTE_AGGREGATION_SCRIPT));
		LOG.debug("Done");

		LOG.debug("Updating upper distribution");
		String udScript = this.readSqlFile(FILE_PATH_PARTY_UPPER_DISTRIBUTION);
		databaseAccessor.executeStatement(udScript.replace(":electionYear",
				"'2005'"));
		databaseAccessor.executeStatement(udScript.replace(":electionYear",
				"'2009'"));
		LOG.debug("Done");

		LOG.debug("Updating lower distribution");
		String ldScript = this.readSqlFile(FILE_PATH_LOWER_DISTRIBUTION);
		databaseAccessor.executeStatement(ldScript.replace(":electionYear",
				"'2005'"));
		databaseAccessor.executeStatement(ldScript.replace(":electionYear",
				"'2009'"));
		LOG.debug("Done");
	}

	@Override
	public List getDirMandates(ElectionYear electionYear) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getListMandates(ElectionYear electionYear) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getOverhangMandates(ElectionYear electionYear) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getSeatDistribution(ElectionYear electionYear) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getStateSeatDistribution(ElectionYear electionYear,
			String partyName) {
		// TODO Auto-generated method stub
		return null;
	}

	private String readSqlFile(String path) throws IOException {
		InputStreamReader reader = new InputStreamReader(this.getClass()
				.getResourceAsStream(path));
		Scanner scanner = new Scanner(reader).useDelimiter("\\Z");
		String res = scanner.next();
		scanner.close();
		reader.close();
		return res;
	}

}
