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
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@Named
@SessionScoped
public class SyncModel {


	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;
	
	@Inject
	IVoteAnalysis voteAnalysis;

	public void launchSync() throws CsvParserException, DatabaseException,
			IOException {
                boolean isGfEnv = ((ServletContext) FacesContext.
                        getCurrentInstance().getExternalContext().getContext()).
                        getServerInfo().startsWith("GlassFish");
		File[] voteFiles = csvToDatabaseSyncer.sync(isGfEnv);
		csvToDatabaseSyncer.sync2(voteFiles);
		voteAnalysis.updateVoteBase();
	}

}
