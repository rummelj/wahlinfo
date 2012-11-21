package com.tu.startup;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.persistence.DatabaseException;

@Stateless
public class CsvToDatabaseSyncerTrigger {

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;

	public void init() {
		try {
			csvToDatabaseSyncer.sync();
		} catch (CsvParserException e) {
			// TODO Log
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Log
			e.printStackTrace();
		}
	}
}
