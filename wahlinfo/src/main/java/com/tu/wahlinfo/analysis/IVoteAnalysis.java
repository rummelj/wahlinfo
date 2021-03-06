package com.tu.wahlinfo.analysis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.Candidate;
import com.tu.wahlinfo.frontend.model.ClosestWinnerOrLoser;
import com.tu.wahlinfo.frontend.model.ElectoralDistrictWinner;
import com.tu.wahlinfo.frontend.model.FederalState;
import com.tu.wahlinfo.frontend.model.OverhangMandate;
import com.tu.wahlinfo.frontend.model.Party;
import com.tu.wahlinfo.frontend.model.PartyDetailVote;
import com.tu.wahlinfo.persistence.DatabaseException;

public interface IVoteAnalysis {

	/**
	 * Updates the aggregated vote values for direct candidates and parties.
	 * Re-calculates all seats per party as well as all mandates
	 * 
	 * @throws DatabaseException
	 *             In case there is a problem while accessing the respective,
	 *             prepared sql files or executing them.
	 * 
	 */
	void updateVoteBase() throws DatabaseException;

	/**
	 * Read from table WIDirectMandateDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	List<Candidate> getDirMandates(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Read from table WIListMandateDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	List<Candidate> getListMandates(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Compare content of WIStatePartySeatDistribution (= nominal seat amount)
	 * and WIDirectMandateDistribution
	 * 
	 * @param electionYear
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 */
	List<OverhangMandate> getOverhangMandates(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Read from table WIPartySeatDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	Map<Party, Integer> getSeatDistribution(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Read from table WIStatePartySeatDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	Map<FederalState, Integer> getStateSeatDistribution(
			ElectionYear electionYear, String partyName)
			throws DatabaseException, IOException;

	/**
	 * Returns the participation in percent.
	 * 
	 * @param electionYear
	 * @param electoralDistrictNumber
	 * @param detailAnalysis
	 */
	float getVoteParticipation(ElectionYear electionYear,
			Integer electoralDistrictNumber, boolean detailAnalysis)
                        throws DatabaseException;

	/**
	 * Returns the name of the candidate that was voted as a direct candidate in
	 * this electoral district and year.
	 * 
	 * @param year
	 * @param detailAnalysis
	 * @param selectedNumber
	 */
	Candidate getVotedDirectCandidate(ElectionYear year,
			Integer electoralDistrictNumber, boolean detailAnalysis)
                        throws DatabaseException;

	/**
	 * Returns all vote details about all parties in this electoral district and
	 * year.
	 * 
	 * @param year
	 * @param detailAnalysis
	 * @param selectedNumber
	 */
	List<PartyDetailVote> getVoteDetails(ElectionYear year,
			Integer electoralDistrictNumber, boolean detailAnalysis)
                        throws DatabaseException, IOException;

	/**
	 * Returns an overview for the winners of every electoral district in a
	 * certain year
	 * 
	 * @return
	 */
	List<ElectoralDistrictWinner> getElectoralDistrictWinnersOverview(
			ElectionYear year)
                        throws DatabaseException, IOException;

	/**
	 * Q6
	 * 
	 * @param electionYear
	 * @return
	 */
	List<ClosestWinnerOrLoser> getClosestWinnerOrLosers(
			ElectionYear electionYear)
                        throws DatabaseException;
}
