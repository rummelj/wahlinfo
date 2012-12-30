package com.tu.wahlinfo.frontend.model;

public class Candidate {

	String name;
	Party party;
	String electoralDistrict;
	String federalState;

	public Candidate() {

	}

	public Candidate(String name, Party party, String electoralDistrict,
			String federalState) {
		super();
		this.name = name;
		this.party = party;
		this.electoralDistrict = electoralDistrict;
		this.federalState = federalState;
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

}
