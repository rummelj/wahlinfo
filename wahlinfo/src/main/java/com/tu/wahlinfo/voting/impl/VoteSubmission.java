package com.tu.wahlinfo.voting.impl;

import com.tu.util.FileScanner;
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
import java.io.IOException;

/**
 * 
 * @author cg
 */
@Stateful
public class VoteSubmission implements IVoteSubmission {

        private static final String FILE_PATH_ORDERED_FIRST_VOTE_SCRIPT = 
                "/sql/SortedElectableDirectCandidates2009ForElectoralDistrict.sql";
        private static final String FILE_PATH_ORDERED_SECOND_VOTE_SCRIPT = 
                "/sql/SortedElectableParties2009ForElectoralDistrict.sql";
        private static final String FILE_PATH_SUBMIT_VOTE_SCRIPT = 
                "/sql/SubmiteVote.sql";
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
		if (electoralDistricts == null) {
			init();
		}
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
		Map<Integer, Candidate> result = new HashMap<>();
                DatabaseResult dbResult;
		try {
                    String query = FileScanner.scanFile(FILE_PATH_ORDERED_FIRST_VOTE_SCRIPT).
                        replace(":electoralDistrictId", database.sanitise(electoralDistrictNumber.toString()));
                        dbResult = databaseAccessor.executeQuery(
                                query, "cName", "pName");			
		} catch (DatabaseException | IOException e) {
			LOG.error("Error retrieving possible first votes", e);
			return result;
		}		
		int rank = 1;		
		for (Map<String, String> row : dbResult) {
			result.put(rank++,
					new Candidate(row.get("cName"), new Party(row.get("pname")),
							"", ""));
		}
		return result;
	}

	private Map<Integer, Party> getPossibleSecondVotes(
			Integer electoralDistrictNumber) {
                Map<Integer, Party> result = new HashMap<>();
                
		DatabaseResult dbResult;
		try {
                    String query = FileScanner.scanFile(FILE_PATH_ORDERED_SECOND_VOTE_SCRIPT).
                        replace(":electoralDistrictId", database.sanitise(electoralDistrictNumber.toString()));
                        dbResult = databaseAccessor.executeQuery(
                                query, "pName");			
		} catch (DatabaseException | IOException e) {
			LOG.error("Error retrieving possible second votes", e);
			return result;
		}		
		int rank = 1;
		for(Map<String,String> row:dbResult){
                    result.put(rank++, new Party(row.get("pName")));
                }

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
		if (votePaper == null || tan == null) {
			return false;
		}

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
		} catch (DatabaseException | IOException e) {
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
			throws DatabaseException, IOException {
            DatabaseResult db;
            String eId = database.sanitise(votePaper.getElectoralDistrictName());
            String cId = null;
            String pId = null;
            
            db = databaseAccessor.executeQuery(""
                    + "select dc.id as cId " 
                    + "WIDirectCandidate dc " 
                    + "where dc.electionYear = '2009' and " 
                    + "dc.name = '" + votePaper.getFirstVote().get(0).getName() + "' ",
                    "cId");
            try{
                cId = db.toList().get(0);
            } catch (UnsupportedOperationException ex){
                throw new DatabaseException("Corrupt vote data given: Either no match or more than one regarding the direct candidate");
            }
            
            db = databaseAccessor.executeQuery(""
                    + "select p.id as pId " 
                    + "WIParty p " 
                    + "where p.electionYear = '2009' and " 
                    + "p.name = '" + votePaper.getSecondVote().get(0).getName() + "' ",
                    "pId");
            try{
                pId = db.toList().get(0);
            } catch (UnsupportedOperationException ex){
                throw new DatabaseException("Corrupt vote data given: Either no match or more than one regarding the party");
            }
            String stmt = FileScanner.scanFile(FILE_PATH_SUBMIT_VOTE_SCRIPT).
                    replace(":electoralDistrictId", eId).
                    replace(":directCandidateId", cId).
                    replace(":partyId", pId).
                    //tar either the valid vote or invalid vote column in WIElectoralDistrictVoteData
                    replace(":ic", (cId == null) ? "in" : "").
                    replace(":ip", (pId == null) ? "in" : "");
            //submit the vote
            databaseAccessor.executeStatement(stmt);
	}

	@Override
	public void closeVote(ElectionYear year) {
            try{
		databaseAccessor.executeStatement(
                        "update WIElection "
                        + "set canVote = FALSE "
                        + "where electionYear = '" + year.toCleanString() + "';");
            } catch (DatabaseException ex){
                LOG.error("Unable to close voting for year " + year.toCleanString());
            }

	}

	@Override
	public void openVote(ElectionYear year) {
		try{
		databaseAccessor.executeStatement(
                        "update WIElection "
                        + "set canVote = TRUE "
                        + "where electionYear = '" + year.toCleanString() + "';");
            } catch (DatabaseException ex){
                LOG.error("Unable to open voting for year " + year.toCleanString());
            }

	}

	@Override
	public boolean isVoteOpen(ElectionYear year) {
                DatabaseResult db;
		try{
		db = databaseAccessor.executeQuery(
                        "select canVote "
                        + "from WIElection "
                        + "where electionYear = '" + year.toCleanString() + "';");
                return db.toList().get(0).equals("t");
            } catch (DatabaseException | UnsupportedOperationException ex){
                LOG.error("Unable to check voting for year " + year.toCleanString());
                return false;
            }		
	}

}
