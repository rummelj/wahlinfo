package com.tu.wahlinfo.frontend.model;

public class PartyDetailVote {

	Party party;
	Integer votesReceived;
	float percentageVotesReceived;
	Integer comparisonToLastYearInVotesReceived;
	float comparisonToLastYearInPercentageVotesReceived;

	public PartyDetailVote(Party party, Integer votesReceived,
			float percentageVotesReceived,
			Integer comparisonToLastYearInVotesReceived,
			float comparisonToLastYearInPercentageVotesReceived) {
		super();
		this.party = party;
		this.votesReceived = votesReceived;
		this.percentageVotesReceived = percentageVotesReceived;
		this.comparisonToLastYearInVotesReceived = comparisonToLastYearInVotesReceived;
		this.comparisonToLastYearInPercentageVotesReceived = comparisonToLastYearInPercentageVotesReceived;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public Integer getVotesReceived() {
		return votesReceived;
	}

	public void setVotesReceived(Integer votesReceived) {
		this.votesReceived = votesReceived;
	}

	public float getPercentageVotesReceived() {
		return percentageVotesReceived;
	}

	public void setPercentageVotesReceived(float percentageVotesReceived) {
		this.percentageVotesReceived = percentageVotesReceived;
	}

	public Integer getComparisonToLastYearInVotesReceived() {
		return comparisonToLastYearInVotesReceived;
	}

	public void setComparisonToLastYearInVotesReceived(
			Integer comparisonToLastYearInVotesReceived) {
		this.comparisonToLastYearInVotesReceived = comparisonToLastYearInVotesReceived;
	}

	public float getComparisonToLastYearInPercentageVotesReceived() {
		return comparisonToLastYearInPercentageVotesReceived;
	}

	public void setComparisonToLastYearInPercentageVotesReceived(
			float comparisonToLastYearInPercentageVotesReceived) {
		this.comparisonToLastYearInPercentageVotesReceived = comparisonToLastYearInPercentageVotesReceived;
	}

}
