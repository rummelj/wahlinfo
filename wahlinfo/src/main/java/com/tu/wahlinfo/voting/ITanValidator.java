package com.tu.wahlinfo.voting;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.model.VotePaper;

public interface ITanValidator {

	/**
	 * Checks if the tan submitted is valid for this voting paper.
	 * 
	 * @param votePaper
	 * @param tan
	 * @throws DatabaseException
	 */
	void validate(VotePaper votePaper, String tan)
			throws IllegalAccessException, DatabaseException;

	/**
	 * Makes a tan ineligible to vote anymore. The district number is passed
	 * within the votePaper because one tan could be valid for multiple
	 * districts and should then only be invalidated for this district.
	 * 
	 * @param electoralDistrictNumber
	 * @param tan
	 * @throws DatabaseException 
	 */
	void invalidate(VotePaper votePaper, String tan) throws DatabaseException;

	/**
	 * Makes all tans in a certain year invalid.
	 * 
	 * @param year
	 * @throws DatabaseException 
	 */
	void invalidateAll(ElectionYear year) throws DatabaseException;
}
