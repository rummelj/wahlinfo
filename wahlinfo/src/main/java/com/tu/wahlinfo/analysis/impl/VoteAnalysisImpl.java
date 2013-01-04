package com.tu.wahlinfo.analysis.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.util.FileScanner;
import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.Candidate;
import com.tu.wahlinfo.frontend.model.ClosestWinnerOrLoser;
import com.tu.wahlinfo.frontend.model.ElectoralDistrictWinner;
import com.tu.wahlinfo.frontend.model.FederalState;
import com.tu.wahlinfo.frontend.model.OverhangMandate;
import com.tu.wahlinfo.frontend.model.Party;
import com.tu.wahlinfo.frontend.model.PartyDetailVote;
import com.tu.wahlinfo.model.DatabaseResult;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

@Stateless
public class VoteAnalysisImpl implements IVoteAnalysis {

	private static final String FILE_PATH_SQL_BASE = "/sql/";
	private static final String FILE_PATH_DIR_VOTE_AGGREGATION_SCRIPT = FILE_PATH_SQL_BASE
			+ "aggregateDirCanVotes.sql";
	private static final String FILE_PATH_LIST_VOTE_AGGREGATION_SCRIPT = FILE_PATH_SQL_BASE
			+ "aggregatePartyVotes.sql";
	private static final String FILE_PATH_OVERHANG_MANDATES_SCRIPT = FILE_PATH_SQL_BASE
			+ "OverhangMandatesForElection.sql";
        private static final String FILE_PATH_PARTY_VOTE_STATISTICS_SCRIPT = FILE_PATH_SQL_BASE
                        + "PartyVoteStatisticsPerElectoralDistrict.sql";
        private static final String FILE_PATH_ELECTORAL_DISTRICT_WINNER_SCRIPT = FILE_PATH_SQL_BASE
                        + "ElectoralDistrictWinnersPerElection.sql";        
	private static final Logger LOG = LoggerFactory
			.getLogger(VoteAnalysisImpl.class);

	private static final String DIRECT_MANDATES_QUERY = "select dc.name, "
			+ "p.name as partyname, " + "ed.name as electoraldistrictname, "
			+ "fs.name as federalstatename " + "from "
			+ "directmandatesview:electionYear dv, " + "widirectcandidate dc, "
			+ "wiparty p, " + "wielectoraldistrict ed, " + "wifederalstate fs "
			+ "where " + "dv.directcandidateid = dc.id and "
			+ "dc.electoraldistrictid = ed.number and "
			+ "dc.partyid = p.id and "
			+ "ed.federalstateid = fs.federalstateid;";

	private static final String LIST_MANDATES_QUERY = "select dc.name, "
			+ "p.name as partyname, " + "ed.name as electoraldistrictname, "
			+ "fs.name as federalstatename " + "from "
			+ "listmandatesview:electionYear lv, " + "widirectcandidate dc, "
			+ "wiparty p, " + "wielectoraldistrict ed, " + "wifederalstate fs "
			+ "where " + "lv.listcandidateid = dc.id and "
			+ "dc.electoraldistrictid = ed.number and "
			+ "dc.partyid = p.id and "
			+ "ed.federalstateid = fs.federalstateid;";

	private static final String SEAT_DISTRIBUTION_QUERY = "select p.name, dv.seats "
			+ "from upperdistributionview:electionYear dv, wiparty p "
			+ "where dv.partyid = p.id;";

	private static final String STATE_SEAT_DISTRIBUTION_QUERY = "select fs.name, dv.seats "
			+ "from lowerdistributionview:electionYear dv, wiparty p, wifederalstate fs "
			+ "where dv.partyid = p.id and p.name=':partyName' and fs.federalstateid = dv.federalstateid;";
        
        private static final String VOTE_PARTICIPATION_QUERY = "select edvd.submittedVotes / (cast (edvd.possibleVotes as float)) as participation "
                + "from WIElectoralDistrictVoteData edvd "
                + "where edvd.electoralDistrictId = :number and edvd.electionYear = ':electionYear';";
        
        private static final String VOTE_PARTICIPATION_DETAIL_QUERY = "with Submitted as ("
                + "select count(*) as votes"
                + "from WIFilledVotingPaper fvp, WIParty p "
                + "where fvp.partyId = p.id and fvp.electoralDistrictId = :number and p.electionYear = ':electionYear' "
                + ") "
                + "select (submitted.votes + edvd.invalidPartyVotes)  / (cast (edvd.possibleVotes as float)) as participation "
                + "from Submitted submitted, WIElectoralDistrictVoteData edvd "
                + "where edvd.electoralDistrictId = :number and edvd.electionYear = ':electionYear'; ";
        
        private static final String VOTED_DIRECT_CANDIDATE_QUERY = "select distinct on (ed.number) dc.name as cName, p.name as pName, ed.name as edName, fs.name as fsName "
                + "from DirectMandatesView:electionYear dmv, WIDirectCandidate dc, WIParty p, WIElectoralDistrict ed, WIFederalState fs "
                + "where dmv.directCandidateId = dc.id and dc.partyId = p.id and dc.electoralDistrictId = ed.number "
                + "and ed.federalStateId = fs.federalStateId and ed.number = :number;";
        
        private static final String VOTED_DIRECT_CANDIDATE_DETAIL_QUERY = "with DistrictVotes as ( "
                + "select fvp.directCandidateId, count(*) as receivedVotes "
                + "from WIFilledVotingPaper fvp , WIDirectCandidate dc "
                + "where fvp.directCandidateId = dc.id and dc.electoralDistrictId = :number and dc.electionYear = ':electionYear' "
                + "group by dc.directCandidateId    "
                + ") "
                + "select distinct on (ed.number) dc.name as cName, p.name as pName, ed.name as edName, fs.name as fsName "
                + "from DistrictVotes dv, WIDirectCandidate dc, WIParty p, WIElectoralDistrict ed, WIFederalState fs "
                + "where dv.directCandidateId = dc.id and dc.partyId = p.id and dc.electoralDistrictId = ed.number "
                + "and ed.federalStateId = fs.federalStateId and dv.receivedVotes = ( select max(dv1.receivedVotes) from DistrictVotes dv1);";                

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
			LOG.info("Vote update finished");
		} catch (IOException ex) {
			throw new DatabaseException(
					"Unable to access files required for vote base update");
		}
	}

	@Override
	public List<Candidate> getDirMandates(ElectionYear electionYear)
			throws DatabaseException, IOException {
		return queryCandidates(electionYear, DIRECT_MANDATES_QUERY);
	}

	@Override
	public List<Candidate> getListMandates(ElectionYear electionYear)
			throws DatabaseException, IOException {
		return queryCandidates(electionYear, LIST_MANDATES_QUERY);
	}

	List<Candidate> queryCandidates(ElectionYear electionYear, String queryToUse)
			throws DatabaseException {
		DatabaseResult queryResult = databaseAccessor.executeQuery(
				queryToUse.replaceAll(":electionYear",
						electionYear.toCleanString()), "name", "partyname",
				"electoraldistrictname", "federalstatename");

		List<Candidate> result = new ArrayList<Candidate>(300);
		for (Map<String, String> queryEntry : queryResult) {
			Candidate temp = new Candidate();
			temp.setElectoralDistrict(queryEntry.get("electoraldistrictname"));
			temp.setFederalState(queryEntry.get("federalstatename"));
			temp.setName(queryEntry.get("name"));
			temp.setParty(new Party(queryEntry.get("partyname")));
			result.add(temp);
		}

		return result;
	}

	@Override
	public List<OverhangMandate> getOverhangMandates(ElectionYear electionYear)
			throws DatabaseException, IOException {
		String query = FileScanner.scanFile(FILE_PATH_OVERHANG_MANDATES_SCRIPT)
				.replaceAll(":electionYear", electionYear.toCleanString());
		DatabaseResult queryResult = databaseAccessor.executeQuery(query,
				"pName", "fsName", "overhangMandates");

		List<OverhangMandate> result = new ArrayList<OverhangMandate>(16);
		for (Map<String, String> queryResultRow : queryResult) {
			result.add(new OverhangMandate(new Party(queryResultRow
					.get("pName")), new FederalState(queryResultRow
					.get("fsName")), Integer.valueOf(queryResultRow
					.get("overhangMandates"))));
		}
		return result;
	}

	@Override
	public Map<Party, Integer> getSeatDistribution(ElectionYear electionYear)
			throws DatabaseException, IOException {
		DatabaseResult queryResult = databaseAccessor.executeQuery(
				SEAT_DISTRIBUTION_QUERY.replaceAll(":electionYear",
						electionYear.toCleanString()), "name", "seats");

		Map<Party, Integer> result = new HashMap<Party, Integer>();
		for (Map<String, String> queryResultRow : queryResult) {
			result.put(new Party(queryResultRow.get("name")),
					Integer.valueOf(queryResultRow.get("seats")));
		}

		return result;
	}

	@Override
	public Map<FederalState, Integer> getStateSeatDistribution(
			ElectionYear electionYear, String partyName)
			throws DatabaseException, IOException {
		DatabaseResult queryResult = databaseAccessor.executeQuery(
				STATE_SEAT_DISTRIBUTION_QUERY.replaceAll(":electionYear",
						electionYear.toCleanString()).replaceAll(":partyName",
						partyName), "name", "seats");

		Map<FederalState, Integer> result = new HashMap<FederalState, Integer>();
		for (Map<String, String> queryResultRow : queryResult) {
			result.put(new FederalState(queryResultRow.get("name")),
					Integer.valueOf(queryResultRow.get("seats")));
		}

		return result;
	}

	@Override
	public float getVoteParticipation(ElectionYear electionYear,
			Integer electoralDistrictNumber, boolean detailAnalysis) 
                        throws DatabaseException{
                String query = null;
		if(detailAnalysis){
                    query = VOTE_PARTICIPATION_DETAIL_QUERY;
                } else {
                    query = VOTE_PARTICIPATION_QUERY;
                }
                query = query.replaceAll(":electionYear", electionYear.toCleanString());
                query = query.replaceAll(":number", Integer.toString(electoralDistrictNumber));
                DatabaseResult queryResult = databaseAccessor.executeQuery(query, "participation");
		return 100 * Float.parseFloat(queryResult.toList().get(0));
	}

	@Override
	public Candidate getVotedDirectCandidate(ElectionYear year,
			Integer electoralDistrictNumber, boolean detailAnalysis) 
                        throws DatabaseException {
		String query = null;
                if(detailAnalysis){
                    query = VOTED_DIRECT_CANDIDATE_DETAIL_QUERY;
                } else {
                    query = VOTED_DIRECT_CANDIDATE_QUERY;
                }
                query = query.replaceAll(":electionYear", year.toCleanString());
                query = query.replaceAll(":number", Integer.toString(electoralDistrictNumber));
                DatabaseResult queryResult = databaseAccessor.executeQuery(query, "cName", "pName", "edName", "fsName");  
                Candidate res = null;
                int counter = 0;
                for(Map<String,String> queryResultRow : queryResult){
                    if(counter >= 1){
                        throw new DatabaseException("Received more than one elected candidate");
                    }
                    res = new Candidate(queryResultRow.get("cName"), new Party(queryResultRow.get("pName")),
                            queryResultRow.get("edName"), queryResultRow.get("fsName"));
                    counter++;
                }
		return res;		
	}

	@Override
	public List<PartyDetailVote> getVoteDetails(ElectionYear year,
			Integer electoralDistrictNumber, boolean detailAnalysis) 
                        throws DatabaseException, IOException {
                //no aggregated data available -> detailAnalysis not important
                String query = FileScanner.scanFile(FILE_PATH_PARTY_VOTE_STATISTICS_SCRIPT);
		DatabaseResult queryResult = databaseAccessor.executeQuery(
                        query.replaceAll(":number", Integer.toString(electoralDistrictNumber)),
                        "pName", "receivedVotes", "percentVotes", "voteDiff", "percentDiff");
                List<PartyDetailVote> details = new ArrayList<PartyDetailVote>();
                for(Map<String,String> queryResultRow: queryResult){
                    details.add(new PartyDetailVote(new Party(queryResultRow.get("pName")), 
                            Integer.parseInt(queryResultRow.get("receivedVotes")), 
                            100 * Float.parseFloat(queryResultRow.get("percentVotes")), 
                            Integer.parseInt(queryResultRow.get("voteDiff")), 
                            100 * Float.parseFloat(queryResultRow.get("percentDiff"))));
                }		
		return details;
	}

	@Override
	public List<ElectoralDistrictWinner> getElectoralDistrictWinnersOverview(
			ElectionYear year) 
                        throws DatabaseException, IOException{
                String query = FileScanner.scanFile(FILE_PATH_ELECTORAL_DISTRICT_WINNER_SCRIPT);
                DatabaseResult queryResult = databaseAccessor.executeQuery(query.replaceAll(
                        ":electionYear", year.toCleanString()), "edName", "directVoteWinner",
                        "listVoteWinner");
                List<ElectoralDistrictWinner> winners = new ArrayList<ElectoralDistrictWinner>();
                for(Map<String,String> queryResultRow: queryResult){
                    winners.add(new ElectoralDistrictWinner(
                            queryResultRow.get("edName"),
                            new Party(queryResultRow.get("firstVoteWinner")),
                            new Party(queryResultRow.get("listVoteWinner"))));
                }		
		return winners;
	}

	@Override
	public List<ClosestWinnerOrLoser> getClosestWinnerOrLosers(
			ElectionYear electionYear) 
                        throws DatabaseException {
		DatabaseResult queryResult = databaseAccessor.executeQuery(
                        "select * from MostConciseWinnersView" + electionYear.toCleanString(),
                        "pName", "cName", "edName", "fsName", "voteDiff", "rank");
		List<ClosestWinnerOrLoser> close = new ArrayList<ClosestWinnerOrLoser>();
                for(Map<String,String> queryResultRow: queryResult){
                         close.add(new ClosestWinnerOrLoser(
                                 new Candidate(queryResultRow.get("cName"), 
                                 new Party(queryResultRow.get("pName")), 
                                 queryResultRow.get("edName"), queryResultRow.get("fsName")),
                                 Integer.parseInt(queryResultRow.get("voteDiff")),
                                 Integer.parseInt(queryResultRow.get("rank"))));
                }
		return close;
	}
}
