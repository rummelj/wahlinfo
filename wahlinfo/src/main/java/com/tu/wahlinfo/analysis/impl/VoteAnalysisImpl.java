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
			Integer electoralDistrictNumber) {
		// TODO Auto-generated method stub
		return 35.7f;
	}

	@Override
	public Candidate getVotedDirectCandidate(ElectionYear year,
			Integer electoralDistrictNumber) {
		// TODO Auto-generated method stub
		return new Candidate("Hans Meier", new Party("APPD"), "Entenhausen",
				"Hintertupfing");
	}

	@Override
	public List<PartyDetailVote> getVoteDetails(ElectionYear year,
			Integer electoralDistrictNumber) {
		// TODO Auto-generated method stub
		List<PartyDetailVote> details = new ArrayList<PartyDetailVote>(3);
		details.add(new PartyDetailVote(new Party("ABC"), 12345, 39.4f, -5000,
				-2.9f));
		details.add(new PartyDetailVote(new Party("DEF"), 8000, 12f, 7500,
				11.3f));
		details.add(new PartyDetailVote(new Party("GHI"), 95421, 48.6f, 4400,
				20.3f));
		return details;
	}

	@Override
	public List<ElectoralDistrictWinner> getElectoralDistrictWinnersOverview(
			ElectionYear year) {
		// TODO Auto-generated method stub
		List<ElectoralDistrictWinner> winners = new ArrayList<ElectoralDistrictWinner>();
		winners.add(new ElectoralDistrictWinner("ADorf", new Party("CDU"),
				new Party("SPD")));
		winners.add(new ElectoralDistrictWinner("BDorf", new Party("SDU"),
				new Party("MPD")));
		winners.add(new ElectoralDistrictWinner("CDorf", new Party("SDM"),
				new Party("LPR")));
		return winners;
	}

	@Override
	public List<ClosestWinnerOrLoser> getClosestWinnerOrLosers(
			ElectionYear electionYear) {
		// TODO: 10 for each party
		List<ClosestWinnerOrLoser> close = new ArrayList<ClosestWinnerOrLoser>();
		// Close winner
		close.add(new ClosestWinnerOrLoser(new Candidate("Hans Meier",
				new Party("CSU"), "ADorf", "Bayern"), 10000, new Candidate(
				"Hans Georg", new Party("SPD"), "Adorf", "Bayern"), 9950));
		// Close loser
		close.add(new ClosestWinnerOrLoser(new Candidate("Hans Meier",
				new Party("CSU"), "ADorf", "Bayern"), 10000, new Candidate(
				"Hans Georg", new Party("SPD"), "Adorf", "Bayern"), 10050));
		return close;
	}
}
