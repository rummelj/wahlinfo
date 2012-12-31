package com.tu.wahlinfo.frontend.model;

public class OverhangMandate {

	Party party;
	FederalState federalState;
	Integer numberOfMandates;

	public OverhangMandate() {
	}

	public OverhangMandate(Party party, FederalState federalState,
			Integer numberOfMandates) {
		super();
		this.party = party;
		this.federalState = federalState;
		this.numberOfMandates = numberOfMandates;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public FederalState getFederalState() {
		return federalState;
	}

	public void setFederalState(FederalState federalState) {
		this.federalState = federalState;
	}

	public Integer getNumberOfMandates() {
		return numberOfMandates;
	}

	public void setNumberOfMandates(Integer numberOfMandates) {
		this.numberOfMandates = numberOfMandates;
	}

}
