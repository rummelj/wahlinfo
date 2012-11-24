package com.tu.wahlinfo.frontend.controller;

import javax.inject.Inject;
import javax.inject.Named;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
public class CsvToDatabaseSyncerLauncher {

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;

	public void launchSync() {
		try {
			csvToDatabaseSyncer.sync();
		} catch (CsvParserException | DatabaseException e) {
			e.printStackTrace();
		}
	}

}
