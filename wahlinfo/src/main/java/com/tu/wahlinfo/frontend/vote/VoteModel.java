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

	String tan;
	String firstVote;
	String secondVote;

	public String getFirstVote() {
		return firstVote;
	}

	public void setFirstVote(String firstVote) {
		this.firstVote = firstVote;
	}

	public String getSecondVote() {
		return secondVote;
	}

	public void setSecondVote(String secondVote) {
		this.secondVote = secondVote;
	}

	public String getTan() {
		return tan;
	}

	public void setTan(String tan) {
		this.tan = tan;
	}

}
