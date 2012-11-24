package com.tu.wahlinfo.csv.parser.impl;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.csv.CsvParserException;
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
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseElections()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseFederalStates()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseElectoralDistricts()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseParties()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseDirectCandidates2005()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseDirectCandidates2009()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseListCandidates2005()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseListCandidates2009()));
		databasePersister.persist(new ArrayList<Persistable>(csvParser
				.parseListCandidates2005()));
	}

}
