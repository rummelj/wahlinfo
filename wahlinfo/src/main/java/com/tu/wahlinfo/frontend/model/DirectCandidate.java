package com.tu.wahlinfo.frontend.model;

public class DirectCandidate {

	String name;
	Party party;
	String electoralDistrict;
	String federalState;
	String percentVotesReceived;

	public DirectCandidate(String name, Party party, String electoralDistrict,
			String federalState, String percentVotesReceived) {
		super();
		this.name = name;
		this.party = party;
		this.electoralDistrict = electoralDistrict;
		this.federalState = federalState;
		this.percentVotesReceived = percentVotesReceived;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public String getElectoralDistrict() {
		return electoralDistrict;
	}

	public void setElectoralDistrict(String electoralDistrict) {
		this.electoralDistrict = electoralDistrict;
	}

	public String getFederalState() {
		return federalState;
	}

	public void setFederalState(String federalState) {
		this.federalState = federalState;
	}

	public String getPercentVotesReceived() {
		return percentVotesReceived;
	}

	public void setPercentVotesReceived(String percentVotesReceived) {
		this.percentVotesReceived = percentVotesReceived;
	}

}
