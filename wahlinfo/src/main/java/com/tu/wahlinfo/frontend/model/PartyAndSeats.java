package com.tu.wahlinfo.frontend.model;

public class PartyAndSeats {

	Party party;
	Integer seats;

	public PartyAndSeats(Party party, Integer seats) {
		this.party = party;
		this.seats = seats;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public String getName() {
		return party.getName();
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

}
