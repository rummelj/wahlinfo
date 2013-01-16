package com.tu.wahlinfo.frontend.controller;

import java.io.File;
import java.io.IOException;

import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.ITanValidator;
import com.tu.wahlinfo.voting.IVoteSubmission;

@Named
@SessionScoped
public class TerminalController {

	private static final Logger LOG = LoggerFactory
			.getLogger(TerminalController.class);

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;

	@Inject
	IVoteAnalysis voteAnalysis;

	@Inject
	IVoteSubmission voteSubmission;

	@Inject
	ITanValidator tanValidator;

	public String handleCommand(String command, String[] params) {
		if (command.equals("launchSync"))
			try {
				launchSync();
				return "Sync complete";
			} catch (CsvParserException | DatabaseException e) {
				LOG.error("", e);
				return e.getMessage();
			}
		else if (command.equals("updateStatistics"))
			try {
				updateVoteBase();
				return "Update statistics complete";
			} catch (DatabaseException | IOException e) {
				LOG.error("", e);
				return e.getMessage();
			}
		else if (command.equals("openVote")) {
			try {
				if (params.length == 0) {
					return "Please provide parameter year!";
				}
				openVote(params[0]);
				return "Vote opened for " + params[0];
			} catch (Exception e) {
				LOG.error("", e);
				return e.getMessage();
			}
		} else if (command.equals("closeVote")) {
			try {
				if (params.length == 0) {
					return "Please provide parameter year!";
				}
				closeVote(params[0]);
				return "Vote closed for " + params[0];
			} catch (Exception e) {
				LOG.error("", e);
				return e.getMessage();
			}
		} else if (command.equals("invalidateTans")) {
			try {
				if (params.length == 0) {
					return "Please provide parameter year!";
				}
				invalidateTans(params[0]);
				return "All tans invalidated for election year " + params[0];
			} catch (Exception e) {
				LOG.error("", e);
				return e.getMessage();
			}
		} else
			return String.format(
					"Erlaubte Kommandos sind '%s', '%s', '%s', '%s' und '%s'.",
					"launchSync", "updateStatistics",
					"openVote <electionYear>", "closeVote <electionYear>",
					"invalidateTans <electionYear>");
	}

	private void invalidateTans(String string) {
		try {
			tanValidator.invalidateAll(ElectionYear.build(string));
		} catch (DatabaseException e) {
			LOG.error("Could not invalidate tans for year {}", string, e);
		}
	}

	private void closeVote(String string) {
		voteSubmission.closeVote(ElectionYear.build(string));
	}

	private void openVote(String string) {
		voteSubmission.openVote(ElectionYear.build(string));
	}

	public void launchSync() throws CsvParserException, DatabaseException {
		boolean isGfEnv = ((ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext()).getServerInfo().startsWith(
				"GlassFish");
		File[] voteFiles = csvToDatabaseSyncer.sync(isGfEnv);
		csvToDatabaseSyncer.sync2(voteFiles);
		voteAnalysis.updateVoteBase();
	}

	public void updateVoteBase() throws DatabaseException, IOException {
		voteAnalysis.updateVoteBase();
	}
}
