package com.tu.wahlinfo.frontend.controller;

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
public class TerminalController {

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;

	@Inject
	IVoteAnalysis voteAnalysis;

	public String handleCommand(String command, String[] params) {
		if (command.equals("launchSync"))
			try {
				launchSync();
			} catch (CsvParserException | DatabaseException e) {
				return e.getMessage();
			}
		else if (command.equals("updateStatistics"))
			try {
				updateVoteBase();
			} catch (DatabaseException | IOException e) {
				return e.getMessage();
			}
		else
			return String.format("Erlaubte Kommandos sind '%s' und '%s'.",
					"launchSync", "updateStatistics");
		return "";
	}

	public void launchSync() throws CsvParserException, DatabaseException {
		File[] voteFiles = csvToDatabaseSyncer.sync();
		for (int k = 0; k < voteFiles.length; k++) {
			csvToDatabaseSyncer.sync2(voteFiles[k], k);
		}
	}

	public void updateVoteBase() throws DatabaseException, IOException {
		voteAnalysis.updateVoteBase();
	}
}
