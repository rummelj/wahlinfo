package com.tu.wahlinfo.voting;

import java.util.Map;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.model.VotePaper;

/**
 * 
 * @author cg
 */
public interface IVoteSubmission {

	Map<String, Integer> getElectoralDistricts(ElectionYear electionYear);

	VotePaper generateVotePaper(ElectionYear electionYear,
			Integer electoralDistrictNumber);

	void vote(VotePaper votePaper, String tan) throws DatabaseException;

	void closeVote(ElectionYear year);

	void openVote(ElectionYear year);

	boolean isVoteOpen(ElectionYear year);

}
