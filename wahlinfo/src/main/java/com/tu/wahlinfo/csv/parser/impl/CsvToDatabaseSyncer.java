package com.tu.wahlinfo.csv.parser.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.common.Observer;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(CsvToDatabaseSyncer.class);	

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
		setProgressInPercent(0);
		ArrayList<Persistable> elections = new ArrayList<Persistable>(
				csvParser.parseElections());
		setProgressInPercent(1);
		ArrayList<Persistable> federalStates = new ArrayList<Persistable>(
				csvParser.parseFederalStates());
		setProgressInPercent(2);
		ArrayList<Persistable> electoralDistricts = new ArrayList<Persistable>(
				csvParser.parseElectoralDistricts());
		setProgressInPercent(3);
		ArrayList<Persistable> parties = new ArrayList<Persistable>(
				csvParser.parseParties());
		setProgressInPercent(4);
		ArrayList<Persistable> directCandidates2005 = new ArrayList<Persistable>(
				csvParser.parseDirectCandidates2005());
		setProgressInPercent(5);
		ArrayList<Persistable> directCandidates2009 = new ArrayList<Persistable>(
				csvParser.parseDirectCandidates2009());
		setProgressInPercent(6);
		ArrayList<Persistable> listCandidates2005 = new ArrayList<Persistable>(
				csvParser.parseListCandidates2005());
		setProgressInPercent(7);
		ArrayList<Persistable> listCandidates2009 = new ArrayList<Persistable>(
				csvParser.parseListCandidates2009());

		setProgressInPercent(9);
		Long totalEntriesToPersist = 0L;
		totalEntriesToPersist += elections.size();
		totalEntriesToPersist += federalStates.size();
		totalEntriesToPersist += electoralDistricts.size();
		totalEntriesToPersist += parties.size();
		totalEntriesToPersist += directCandidates2005.size();
		totalEntriesToPersist += directCandidates2009.size();
		totalEntriesToPersist += listCandidates2005.size();
		totalEntriesToPersist += listCandidates2009.size();
		// For electoralDistrcitVotes
		totalEntriesToPersist += 2 * 40 * 1000 * 1000;

		Long entriesPersisted = 0L;
		databasePersister.persist(elections);
		entriesPersisted += elections.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(federalStates);
		entriesPersisted += federalStates.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(electoralDistricts);
		entriesPersisted += electoralDistricts.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(parties);
		entriesPersisted += parties.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(directCandidates2005);
		entriesPersisted += directCandidates2005.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(directCandidates2009);
		entriesPersisted += directCandidates2009.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(listCandidates2005);
		entriesPersisted += listCandidates2005.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		databasePersister.persist(listCandidates2009);
		entriesPersisted += listCandidates2009.size();
		setProgressInPercent((int) (10 + (entriesPersisted * 90)
				/ totalEntriesToPersist));

		return csvParser.parseVotesToFiles();
	}

	@Override
	public void sync2(File file, int k) throws CsvParserException,
			DatabaseException {		
		System.gc();
		setProgressInPercent(15);
		

		LOG.debug("PARSING FILE " + k);
		copyFileToDatabase(file);
		file.deleteOnExit();
		setProgressInPercent(k * 20 - 1);

	}			

	private void copyFileToDatabase(File f) throws DatabaseException {
		String filePath = f.getAbsolutePath();
		databaseAccessor
				.executeStatement("COPY WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid) FROM '"
						+ filePath + "' WITH (FORMAT CSV, DELIMITER ';')");
	}

	private void setProgressInPercent(int percentage) {
		this.progressInPercent = percentage;
		notifyObservers();
	}

	private void notifyObservers() {
		for (Observer observer : observers) {
			observer.notify(this);
		}
	}

	@Override
	public int getProgressInPercent() {
		return progressInPercent;
	}

	@Override
	public void register(Observer observer) {
		observers.add(observer);
	}
}
