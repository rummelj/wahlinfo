package com.tu.wahlinfo.analysis;

import java.util.List;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;

public interface VoteAnalyse {

	/**
	 * Executes scripts aggregateDirCanVotes.sql and aggregatePartyVotes.sql
	 * which count the received votes per candidate respectively party.
	 */
	void updateStatistics();

	/**
	 * Executes CleanMandatesDistributionForElection.sql and
	 * MandatesDistributionForElection.sql
	 * 
	 * @param electionYear
	 * @return
	 */
	List getMandataDistribution(ElectionYear electionYear);
}
