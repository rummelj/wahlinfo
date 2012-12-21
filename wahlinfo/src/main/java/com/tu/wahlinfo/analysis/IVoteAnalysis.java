package com.tu.wahlinfo.analysis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.DirectCandidate;
import com.tu.wahlinfo.frontend.model.FederalState;
import com.tu.wahlinfo.frontend.model.Party;
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
	List<DirectCandidate> getDirMandates(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Read from table WIListMandateDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	List getListMandates(ElectionYear electionYear) throws DatabaseException,
			IOException;

	/**
	 * Compare content of WIStatePartySeatDistribution (= nominal seat amount)
	 * and WIDirectMandateDistribution
	 * 
	 * @param electionYear
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 */
	Map<Party, Integer> getOverhangMandates(ElectionYear electionYear)
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
}
