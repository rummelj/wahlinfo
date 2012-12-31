package com.tu.wahlinfo.csv.parser.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.util.FileScanner;
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

	private static final String VOTE_IMPORT_SCRIPT_PATH = "/sql/ImportVotes.sql";
        private static final String CREATE_INDICES_SCRIPT_PATH = "/sql/CreateIndices.sql";
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
	public File[] sync(boolean isGfEnv) throws CsvParserException, DatabaseException {
		LOG.info("Import phase 1");
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
		LOG.info("Import phase 1 complete");
		return csvParser.parseVotesToFiles(isGfEnv);
	}

	@Override
	public void sync2(File[] files) throws CsvParserException,
			DatabaseException{
		// 5 files
                LOG.info("Garbage collection");
                System.gc();
                LOG.info("Import phase 2");
		try {						
			String query = FileScanner.scanFile(VOTE_IMPORT_SCRIPT_PATH);
			for (int k = 0; k < 5; k++) {
				query = query.replace(":filePath" + k,
						files[k].getAbsolutePath());
			}
			databaseAccessor.executeStatement(query);
			LOG.info("Done. Scheduling file deletion");
			for (File file : files) {
				file.delete();
			}
                        LOG.info("Done. Creating indices. This should take about 10 minutes");
                        databaseAccessor.executeStatement(FileScanner.scanFile(CREATE_INDICES_SCRIPT_PATH));
			LOG.info("Done. Vote sync is complete");
                        LOG.info("Please manually run 'vacuum' and 'analyze' against the db to ensure proper performance");

		} catch (IOException ex) {
			throw new DatabaseException("Unable to access sql vote import file");
		}
	}

}
