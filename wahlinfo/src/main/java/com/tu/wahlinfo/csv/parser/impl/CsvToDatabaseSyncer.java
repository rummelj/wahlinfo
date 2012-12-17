package com.tu.wahlinfo.csv.parser.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.parser.ICsvParser;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.model.Persistable;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.IdGenerator;

@Stateless
public class CsvToDatabaseSyncer implements ICsvToDatabaseSyncer {

	private static final Logger LOG = LoggerFactory
			.getLogger(CsvToDatabaseSyncer.class);

	@Inject
	ICsvParser csvParser;

	@Inject
	IdGenerator idGenerator;

	@Inject
	DatabasePersister databasePersister;

	@Inject
	DatabaseAccessor databaseAccessor;

	Set<Observer> observers;

	int progressInPercent;

	@PostConstruct
	public void init() {
		observers = new HashSet<Observer>();
	}

	@Override
	public File[] sync() throws CsvParserException, DatabaseException {
		ArrayList<Persistable> elections = new ArrayList<Persistable>(
				csvParser.parseElections());
		ArrayList<Persistable> federalStates = new ArrayList<Persistable>(
				csvParser.parseFederalStates());
		ArrayList<Persistable> electoralDistricts = new ArrayList<Persistable>(
				csvParser.parseElectoralDistricts());
		ArrayList<Persistable> parties = new ArrayList<Persistable>(
				csvParser.parseParties());
		ArrayList<Persistable> directCandidates2005 = new ArrayList<Persistable>(
				csvParser.parseDirectCandidates2005());
		ArrayList<Persistable> directCandidates2009 = new ArrayList<Persistable>(
				csvParser.parseDirectCandidates2009());
		ArrayList<Persistable> listCandidates2005 = new ArrayList<Persistable>(
				csvParser.parseListCandidates2005());
		ArrayList<Persistable> listCandidates2009 = new ArrayList<Persistable>(
				csvParser.parseListCandidates2009());
		databasePersister.persist(elections);
		databasePersister.persist(federalStates);
		databasePersister.persist(electoralDistricts);
		databasePersister.persist(parties);
		databasePersister.persist(directCandidates2005);
		databasePersister.persist(directCandidates2009);
		databasePersister.persist(listCandidates2005);
		databasePersister.persist(listCandidates2009);
		return csvParser.parseVotesToFiles();
	}

	@Override
	public void sync2(File file, int k) throws CsvParserException,
			DatabaseException {
		System.gc();
		LOG.debug("PARSING FILE " + k);
		copyFileToDatabase(file);
		file.deleteOnExit();

	}

	private void copyFileToDatabase(File f) throws DatabaseException {
		String filePath = f.getAbsolutePath();
		databaseAccessor
				.executeStatement("COPY WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid) FROM '"
						+ filePath + "' WITH (FORMAT CSV, DELIMITER ';')");
	}

}
