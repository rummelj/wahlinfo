package com.tu.wahlinfo.voting.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.tu.wahlinfo.persistence.Database;
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

	@Inject
	Database database;

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
		try {
			DatabaseResult dbResult = databaseAccessor
					.executeQuery(
							"select c.name as cname, c.rank, p.name as pname from WIListCandidate c, WIElectoralDistrict e, WIParty p where c.federalstateid = e.federalstateid and c.partyid = p.id and c.electionyear='2009' and e.number="
									+ database.sanitise(electoralDistrictNumber
											.toString()) + ";", "name", "rank",
							"pname");

			Map<Party, List<Candidate>> result = new HashMap<Party, List<Candidate>>();
			for (Map<String, String> row : dbResult) {
				String name = row.get("name");
				String pname = row.get("pname");
				if (!result.containsKey(pname)) {
					result.put(new Party(pname), new LinkedList<Candidate>());
				}
				result.get(new Party(pname)).add(
						new Candidate(name, new Party(pname), "", ""));
			}
			return result;
		} catch (DatabaseException e) {
			LOG.error("Error retrieving list candidates", e);
			return new HashMap<Party, List<Candidate>>();
		}

	}

	private Map<Integer, Candidate> getPossibleFirstVotes(
			Integer electoralDistrictNumber) {
		DatabaseResult dbResult;
		try {
			dbResult = databaseAccessor
					.executeQuery(
							"select c.name as cname, p.name as pname from WIDirectCandidate c, WIParty p where c.electionyear = '2009' and (c.partyid = null or c.partyid = p.id) and c.electoraldistrictid = "
									+ database.sanitise(electoralDistrictNumber
											.toString()) + ";", "name", "pname");
		} catch (DatabaseException e) {
			LOG.error("Error retrieving possible first votes", e);
			return new HashMap<Integer, Candidate>();
		}
		// TODO: Get rank!
		int rank = 1;
		Map<Integer, Candidate> result = new HashMap<Integer, Candidate>();
		for (Map<String, String> row : dbResult) {
			result.put(rank++,
					new Candidate(row.get("name"), new Party(row.get("pname")),
							"", ""));
		}
		return result;
	}

	private Map<Integer, Party> getPossibleSecondVotes(
			Integer electoralDistrictNumber) {
		// NOTE: Think about caching this similar to electoralDistricts
		// TODO Auto-generated method stub
		Map<Integer, Party> result = new HashMap<Integer, Party>();
		result.put(1, new Party("CSU"));
		result.put(2, new Party("GRUENE"));
		result.put(3, new Party("FDP"));
		result.put(4, new Party("SPD"));

		return result;
	}

	/**
	 * This method must be synchronized to avoid race conditions and more votes
	 * with one tan.
	 * 
	 * @throws DatabaseException
	 */
	@Override
	public synchronized boolean vote(VotePaper votePaper, String tan)
			throws DatabaseException {
		if (!isVoteOpen(votePaper.getElectionYear())) {
			LOG.error(
					"Tried to vote an an election that is closed. Vote rejected. {}",
					votePaper.getElectionYear());
			return false;
		}

		try {
			tanValidator.validate(votePaper, tan);
		} catch (IllegalAccessException | DatabaseException e) {
			LOG.error(
					"A tan {} not authorised to vote was found. Vote was rejected!",
					tan);
			return false;
		}

		try {
			persistVoteInDatabase(votePaper);
		} catch (DatabaseException e) {
			LOG.error("Could not vote (Tan = {}): {}", tan, votePaper);
			LOG.error("No vote was persisted. Tan is still valid.");
			return false;
		}
		LOG.info("Succesfully voted (Tan = {}): {} ", tan, votePaper);

		tanValidator.invalidate(votePaper, tan);
		LOG.info("Succesfully invalidated tan {}", tan);
		return true;
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
		return true;
	}

}
