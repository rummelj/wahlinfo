package com.tu.wahlinfo.voting.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.util.Crypto;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.Database;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.ITanValidator;
import com.tu.wahlinfo.voting.model.VotePaper;

@Stateless
public class TanValidator implements ITanValidator {

	@Inject
	Database database;

	@Inject
	DatabaseAccessor databaseAccessor;

	@Override
	public void validate(VotePaper votePaper, String tan)
			throws IllegalAccessException, DatabaseException {
		String tanSha1 = database.sanitise(Crypto.sha1(tan));
		String year = database.sanitise(votePaper.getElectionYear()
				.toCleanString());
		String electoralDistrict = database.sanitise(votePaper
				.getElectoralDistrictNumber().toString());

		boolean valid = databaseAccessor
				.executeQuery(
						String.format(
								"select count(*) from WITan where year = %s and electoralDistrict = %s and tan = %s;",
								year, electoralDistrict, tanSha1), "count")
				.iterator().next().get("count").equals("1");

		if (!valid) {
			throw new IllegalAccessException("This tan is not valid!");
		}
	}

	@Override
	public void invalidate(VotePaper votePaper, String tan)
			throws DatabaseException {
		String tanSha1 = database.sanitise(Crypto.sha1(tan));
		String year = database.sanitise(votePaper.getElectionYear()
				.toCleanString());
		String electoralDistrict = database.sanitise(votePaper
				.getElectoralDistrictNumber().toString());

		databaseAccessor
				.executeStatement(String
						.format("DELETE FROM WITan WHERE year = %s and electoralDistrict = %s and tan = %s",
								year, electoralDistrict, tanSha1));
	}

	@Override
	public void invalidateAll(ElectionYear year) throws DatabaseException {
		String yearClean = database.sanitise(year.toCleanString());

		databaseAccessor.executeStatement(String.format(
				"DELETE FROM WITan WHERE year = %s", yearClean));
	}

}
