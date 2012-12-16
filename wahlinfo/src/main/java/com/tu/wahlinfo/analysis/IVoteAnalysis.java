package com.tu.wahlinfo.analysis;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.DataBindingException;

import org.mockito.internal.stubbing.answers.ThrowsException;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.DatabaseException;

public interface IVoteAnalysis {

	/**
	 * Updates the aggregated vote values for direct candidates and parties.
	 * Re-calculates all seats per party as well as all mandates
	 * 
	 * @throws DatabaseException
	 * @throws IOException
	 *             In case there is a problem while accessing the respective,
	 *             prepared sql files.
	 */
	void updateVoteBase() throws DatabaseException, IOException;

	/**
	 * Read from table WIDirectMandateDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	List getDirMandates(ElectionYear electionYear) throws DatabaseException,
			IOException;

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
	List getOverhangMandates(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Read from table WIPartySeatDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	List getSeatDistribution(ElectionYear electionYear)
			throws DatabaseException, IOException;

	/**
	 * Read from table WIStatePartySeatDistribution
	 * 
	 * @param electionYear
	 * @return
	 */
	List getStateSeatDistribution(ElectionYear electionYear, String partyName)
			throws DatabaseException, IOException;
}
