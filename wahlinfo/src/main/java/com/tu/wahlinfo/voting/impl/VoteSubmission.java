package com.tu.wahlinfo.voting.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.Candidate;
import com.tu.wahlinfo.frontend.model.Party;
import com.tu.wahlinfo.model.DatabaseResult;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.ITanValidator;
import com.tu.wahlinfo.voting.IVoteSubmission;
import com.tu.wahlinfo.voting.model.VotePaper;

/**
 * 
 * @author cg
 */
@Stateful
public class VoteSubmission implements IVoteSubmission {

	private static final Logger LOG = LoggerFactory
			.getLogger(VoteSubmission.class);

	@Inject
	DatabaseAccessor databaseAccessor;

	@Inject
	ITanValidator tanValidator;

	Map<String, Integer> electoralDistricts;

	@PostConstruct
	public void init() {
		try {
			initElectoralDistricts();
		} catch (DatabaseException e) {
			LOG.error("Could not initialise electoral districts", e);
		}
	}

	@Override
	public Map<String, Integer> getElectoralDistricts(ElectionYear electionYear) {
		return electoralDistricts;
	}

	void initElectoralDistricts() throws DatabaseException {
		electoralDistricts = new HashMap<String, Integer>(299);
		DatabaseResult dbResult = databaseAccessor.executeQuery(
				"select number as id, name from WIElectoralDistrict;", "id",
				"name");
		for (Map<String, String> electoralDistrict : dbResult) {
			electoralDistricts.put(electoralDistrict.get("name"),
					Integer.valueOf(electoralDistrict.get("id")));
		}
	}

	@Override
	public VotePaper generateVotePaper(ElectionYear electionYear,
			Integer electoralDistrictNumber) {
		VotePaper vP = new VotePaper(electionYear, electoralDistrictNumber);
		vP.setElectionDate(new Date());
		vP.setElectoralDistrictName(findElectoralDistrictName(electoralDistrictNumber));
		vP.setListCandidates(getListCandidates(electoralDistrictNumber));
		vP.setPossibleFirstVotes(getPossibleFirstVotes(electoralDistrictNumber));
		vP.setPossibleSecondVotes(getPossibleSecondVotes(electoralDistrictNumber));
		return vP;
	}

	private String findElectoralDistrictName(Integer electoralDistrictNumber) {
		for (Entry<String, Integer> electoralDistrict : electoralDistricts
				.entrySet()) {
			if (electoralDistrict.getValue() == electoralDistrictNumber) {
				return electoralDistrict.getKey();
			}
		}
		return null;
	}

	private Map<Party, List<Candidate>> getListCandidates(
			Integer electoralDistrictNumber) {
		// NOTE: Think about caching this similar to electoralDistricts
		// TODO Auto-generated method stub
		return null;
	}

	private Map<Integer, Candidate> getPossibleFirstVotes(
			Integer electoralDistrictNumber) {
		// NOTE: Think about caching this similar to electoralDistricts
		// TODO Auto-generated method stub
		return null;
	}

	private Map<Integer, Party> getPossibleSecondVotes(
			Integer electoralDistrictNumber) {
		// NOTE: Think about caching this similar to electoralDistricts
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method must be synchronized to avoid race conditions and more votes
	 * with one tan.
	 * 
	 * @throws DatabaseException
	 */
	@Override
	public synchronized void vote(VotePaper votePaper, String tan)
			throws DatabaseException {
		if (!isVoteOpen(votePaper.getElectionYear())) {
			LOG.error(
					"Tried to vote an an election that is closed. Vote rejected. {}",
					votePaper.getElectionYear());
			return;
		}

		try {
			tanValidator.validate(votePaper, tan);
		} catch (IllegalAccessException | DatabaseException e) {
			LOG.error(
					"A tan {} not authorised to vote was found. Vote was rejected!",
					tan);
			return;
		}

		try {
			persistVoteInDatabase(votePaper);
		} catch (DatabaseException e) {
			LOG.error("Could not vote (Tan = {}): {}", tan, votePaper);
			LOG.error("No vote was persisted. Tan is still valid.");
			return;
		}
		LOG.info("Succesfully voted (Tan = {}): {} ", tan, votePaper);

		tanValidator.invalidate(votePaper, tan);
		LOG.info("Succesfully invalidated tan {}", tan);
	}

	/**
	 * This method must be transactional. If it throws an exception there must
	 * not be any vote persisted.
	 * 
	 * @param votePaper
	 */
	private void persistVoteInDatabase(VotePaper votePaper)
			throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeVote(ElectionYear year) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openVote(ElectionYear year) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVoteOpen(ElectionYear year) {
		// TODO Auto-generated method stub
		return false;
	}

}
