package com.tu.wahlinfo.csv.parser;

import java.io.File;
import java.util.Collection;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.CsvDirectCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvElection;
import com.tu.wahlinfo.csv.entities.impl.CsvElectoralDistrict;
import com.tu.wahlinfo.csv.entities.impl.CsvFederalState;
import com.tu.wahlinfo.csv.entities.impl.CsvListCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvParty;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;

/**
 * Defines functionality of a csv parser. Any implementation most likely relies
 * on the header structure of the internally available csvs.
 * 
 * @author cg
 */
public interface ICsvParser {

	/**
	 * Parses all votes to several files prefixed by the constant of the class
	 * "AbstractVoteFileGenerator".
	 * 
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv files.
	 */
	public File[] parseVotesToFiles() throws CsvParserException;

	/**
	 * Parses all available parties from the internally available candidate
	 * csvs.
	 * 
	 * @return A list of all parties with generated ids starting from 0.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv files.
	 */
	public Collection<CsvParty> parseParties() throws CsvParserException;

	/**
	 * Parses all vote aggregations from an internally available csv. The parser
	 * does not store respective results. Also parties are parse beforehand to
	 * correctly assign candidate party ids.
	 * 
	 * @param startElId
	 *            Limits the number electoral districts to be included. Any
	 *            district having an id smaller than this value will be ignored.
	 * @param endELId
	 *            Limits the number electoral districts to be included. Any
	 *            district having an id greater than this value will be ignored.
	 * @return A collection of the created vote aggregation objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvVoteAggregation> parseVoteAggregations(int startElId,
			int endELId) throws CsvParserException;

	/**
	 * Parses all federal states from an internally available csv file. Due to
	 * the fact that federal states and electoral districts are contained in the
	 * same file, both are parsed together and stored within the parser
	 * (consequently, further invocations of any of the respective methods do
	 * not lead to an additional parsing). States are thereby recognized by the
	 * id pattern "9**".
	 * 
	 * @return A collection of the created federal state objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvFederalState> parseFederalStates()
			throws CsvParserException;

	/**
	 * Parses all electoral districts from an internally available csv file. Due
	 * to the fact that federal states and electoral districts are contained in
	 * the same file, both are parsed together and stored within the parser
	 * (consequently, further invocations of any of the respective methods do
	 * not lead to an additional parsing). States are thereby recognized by the
	 * id pattern "9**". Additionally, the district having the id="999" will be
	 * ignored as it represents the whole country
	 * 
	 * @return A collection of the created electoral district objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvElectoralDistrict> parseElectoralDistricts()
			throws CsvParserException;

	/**
	 * Parses all direct candidates of 2005 from an internally available csv
	 * file. Due to the fact that list candidates of the same year are stored
	 * within the same file, both are parsed together and stored within the
	 * parser (consequently, further invocations of any of the respective
	 * methods do not lead to an additional parsing). In order to correctly
	 * assign federal state ids, electoral districts and federal states must
	 * have been or are actively parsed before the execution of this method.
	 * Also parties are parse beforehand to correctly assign candidate party
	 * ids.
	 * 
	 * @return A collection of the create direct candidate objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvDirectCandidate> parseDirectCandidates2005()
			throws CsvParserException;

	/**
	 * Parses all list candidates of 2005 from an internally available csv file.
	 * Due to the fact that direct candidates of the same year are stored within
	 * the same file, both are parsed together and stored within the parser
	 * (consequently, further invocations of any of the respective methods do
	 * not lead to an additional parsing). In order to correctly assign federal
	 * state ids, electoral districts and federal states must have been or are
	 * actively parsed before the execution of this method. Also parties are
	 * parse beforehand to correctly assign candidate party ids.
	 * 
	 * @return A collection of the create list candidate objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvListCandidate> parseListCandidates2005()
			throws CsvParserException;

	/**
	 * Parses all direct candidates of 2009 from an internally available csv
	 * file. Due to the fact that list candidates of the same year are stored
	 * within the same file, both are parsed together and stored within the
	 * parser (consequently, further invocations of any of the respective
	 * methods do not lead to an additional parsing). In order to correctly
	 * assign federal state ids, electoral districts and federal states must
	 * have been or are actively parsed before the execution of this method.
	 * Also parties are parse beforehand to correctly assign candidate party
	 * ids.
	 * 
	 * @return A collection of the create direct candidate objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvDirectCandidate> parseDirectCandidates2009()
			throws CsvParserException;

	/**
	 * Parses all list candidates of 2009 from an internally available csv file.
	 * Due to the fact that direct candidates of the same year are stored within
	 * the same file, both are parsed together and stored within the parser
	 * (consequently, further invocations of any of the respective methods do
	 * not lead to an additional parsing). In order to correctly assign federal
	 * state ids, electoral districts and federal states must have been or are
	 * actively parsed before the execution of this method. Also parties are
	 * parse beforehand to correctly assign candidate party ids.
	 * 
	 * @return A collection of the create list candidate objects.
	 * @throws CsvParserException
	 *             In case an exception occurred while accessing the csv file.
	 */
	public Collection<CsvListCandidate> parseListCandidates2009()
			throws CsvParserException;

	/**
	 * Returns all elections occuring in the csvs.
	 * 
	 * @return
	 */
	public Collection<CsvElection> parseElections();

}
