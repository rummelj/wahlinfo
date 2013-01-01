package com.tu.wahlinfo.frontend.model;

public class ClosestWinnerOrLoser {

	Candidate candidate;
	Integer votesReceived;
	Candidate opponent;
	Integer opponentVotesReceived;

	public ClosestWinnerOrLoser() {

	}

	public ClosestWinnerOrLoser(Candidate candidate, Integer votesReceived,
			Candidate opponent, Integer opponentVotesReceived) {
		super();
		this.candidate = candidate;
		this.votesReceived = votesReceived;
		this.opponent = opponent;
		this.opponentVotesReceived = opponentVotesReceived;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Integer getVotesReceived() {
		return votesReceived;
	}

	public void setVotesReceived(Integer votesReceived) {
		this.votesReceived = votesReceived;
	}

	public Candidate getOpponent() {
		return opponent;
	}

	public void setOpponent(Candidate opponent) {
		this.opponent = opponent;
	}

	public Integer getOpponentVotesReceived() {
		return opponentVotesReceived;
	}

	public void setOpponentVotesReceived(Integer opponentVotesReceived) {
		this.opponentVotesReceived = opponentVotesReceived;
	}
}
