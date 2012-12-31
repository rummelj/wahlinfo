package com.tu.wahlinfo.frontend.model;

public class ElectoralDistrictWinner {

	String electoralDistrict;
	Party winnerFirstVote;
	Party winnerSecondVote;

	public ElectoralDistrictWinner() {

	}

	public ElectoralDistrictWinner(String electoralDistrict,
			Party winnerFirstVote, Party winnerSecondVote) {
		super();
		this.electoralDistrict = electoralDistrict;
		this.winnerFirstVote = winnerFirstVote;
		this.winnerSecondVote = winnerSecondVote;
	}

	public String getElectoralDistrict() {
		return electoralDistrict;
	}

	public void setElectoralDistrict(String electoralDistrict) {
		this.electoralDistrict = electoralDistrict;
	}

	public Party getWinnerFirstVote() {
		return winnerFirstVote;
	}

	public void setWinnerFirstVote(Party winnerFirstVote) {
		this.winnerFirstVote = winnerFirstVote;
	}

	public Party getWinnerSecondVote() {
		return winnerSecondVote;
	}

	public void setWinnerSecondVote(Party winnerSecondVote) {
		this.winnerSecondVote = winnerSecondVote;
	}

}
