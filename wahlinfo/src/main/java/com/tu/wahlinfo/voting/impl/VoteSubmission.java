package com.tu.wahlinfo.voting.impl;

import java.util.Map;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.voting.IVoteSubmission;
import com.tu.wahlinfo.voting.model.VotePaper;

import javax.ejb.Stateless;

/**
 * 
 * @author cg
 */
@Stateless
public class VoteSubmission implements IVoteSubmission {

	@Override
	public Map<String, Integer> getElectoralDistricts(ElectionYear electionYear) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VotePaper generateVotePaper(ElectionYear electionYear,
			Integer electoralDistrictNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void vote(VotePaper votePaper, String tan) {
		// TODO Auto-generated method stub

	}

}
