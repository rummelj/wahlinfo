package com.tu.wahlinfo.voting;

import com.tu.wahlinfo.voting.model.VotePaper;

public interface ITanValidator {

	/**
	 * Checks if the tan submitted is valid for this voting paper.
	 * 
	 * @param votePaper
	 * @param tan
	 */
	void validate(VotePaper votePaper, String tan)
			throws IllegalAccessException;

	/**
	 * Makes a tan ineligible to vote anymore. The district number is passed
	 * because one tan could be valid for multiple districts and should then
	 * only be invalidated for this district.
	 * 
	 * @param electoralDistrictNumber
	 * @param tan
	 */
	void invalidate(Integer electoralDistrictNumber, String tan);

}
