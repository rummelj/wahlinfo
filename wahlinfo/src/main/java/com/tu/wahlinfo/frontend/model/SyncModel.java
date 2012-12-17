package com.tu.wahlinfo.frontend.model;

import java.io.File;
import java.io.IOException;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
@SessionScoped
public class SyncModel {

	int progress;

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;
	
	@Inject
	IVoteAnalysis voteAnalysis;

	public void launchSync() throws CsvParserException, DatabaseException,
			IOException {
		File[] voteFiles = csvToDatabaseSyncer.sync();
		csvToDatabaseSyncer.sync2(voteFiles);
		voteAnalysis.updateVoteBase();
	}

}
