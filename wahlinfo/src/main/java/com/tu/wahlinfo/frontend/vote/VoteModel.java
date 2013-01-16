package com.tu.wahlinfo.frontend.vote;

import java.io.Serializable;

import javax.enterprise.inject.Model;

import com.tu.wahlinfo.voting.model.VotePaper;

@Model
public class VoteModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1738249439881635100L;

	VotePaper votePaper;
	String tan;

	public VotePaper getVotePaper() {
		return votePaper;
	}

	public void setVotePaper(VotePaper votePaper) {
		this.votePaper = votePaper;
	}

	public String getTan() {
		return tan;
	}

	public void setTan(String tan) {
		this.tan = tan;
	}

}
