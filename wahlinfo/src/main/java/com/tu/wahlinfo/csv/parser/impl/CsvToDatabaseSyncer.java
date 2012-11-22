package com.tu.wahlinfo.csv.parser.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.CsvDirectCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvElectoralDistrict;
import com.tu.wahlinfo.csv.entities.impl.CsvFederalState;
import com.tu.wahlinfo.csv.entities.impl.CsvListCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvParty;
import com.tu.wahlinfo.csv.parser.ICsvParser;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.model.Persistable;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;

@Stateless
public class CsvToDatabaseSyncer implements ICsvToDatabaseSyncer {

	@Inject
	ICsvParser csvParser;

	@Inject
	DatabasePersister databasePersister;

	@Override
	public void sync() throws CsvParserException, DatabaseException {
		Collection<CsvElectoralDistrict> electoralDistricts = csvParser
				.parseElectoralDistricts();
		Collection<CsvFederalState> federalStates = csvParser
				.parseFederalStates();
		Collection<CsvParty> parties = csvParser.parseParties();
		Collection<CsvDirectCandidate> directCandidates2005 = csvParser
				.parseDirectCandidates2005();
		Collection<CsvDirectCandidate> directCandidates2009 = csvParser
				.parseDirectCandidates2009();

		Collection<CsvListCandidate> listCandidates2005 = csvParser
				.parseListCandidates2005();
		Collection<CsvListCandidate> listCandidates2009 = csvParser
				.parseListCandidates2009();
		Collection<Persistable> allEntities = new ArrayList<Persistable>(1024);
		allEntities.addAll(electoralDistricts);
		allEntities.addAll(federalStates);
		allEntities.addAll(parties);
		allEntities.addAll(directCandidates2005);
		allEntities.addAll(directCandidates2009);
		allEntities.addAll(listCandidates2005);
		allEntities.addAll(listCandidates2009);

		databasePersister.persist(allEntities);
	}

}
