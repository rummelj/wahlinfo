package com.tu.wahlinfo.analysis.impl;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.util.FileScanner;
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
	private static final Logger LOG = LoggerFactory
			.getLogger(VoteAnalysisImpl.class);

	@Inject
	DatabaseAccessor databaseAccessor;

	@Override
	public void updateVoteBase() throws DatabaseException {
		try {
			LOG.info("Starting vote base update");
			LOG.debug("Updating party votes");
			databaseAccessor.executeStatement(FileScanner
					.scanFile(FILE_PATH_LIST_VOTE_AGGREGATION_SCRIPT));
			LOG.debug("Done");

			LOG.debug("Updating direct candidate votes");
			databaseAccessor.executeStatement(FileScanner
					.scanFile(FILE_PATH_DIR_VOTE_AGGREGATION_SCRIPT));
			LOG.debug("Done. Performing database cleanup.");
			this.databaseAccessor.vacuumAndAnalyze("WIPartyVotes",
					"WIDirectCandidate");
			LOG.info("Vote update finished");
		} catch (IOException ex) {
			throw new DatabaseException(
					"Unable to access files required for vote base update");
		}
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

}
