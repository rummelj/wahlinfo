package com.tu.wahlinfo.frontend.model;

import java.io.IOException;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
@RequestScoped
public class AnalysisModel {
	
	@Inject
	IVoteAnalysis voteAnalysis;

	public void updateVoteBase() throws DatabaseException, IOException {
		voteAnalysis.updateVoteBase();
	}

}
