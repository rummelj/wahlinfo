package com.tu.wahlinfo.frontend.model;

import java.io.File;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
@SessionScoped
public class SyncModel {

	int progress;

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;

	public void launchSync() throws CsvParserException, DatabaseException {
		File[] voteFiles = csvToDatabaseSyncer.sync();
		for (int k = 0; k < voteFiles.length; k++) {
			csvToDatabaseSyncer.sync2(voteFiles[k], k);
		}
	}

}
