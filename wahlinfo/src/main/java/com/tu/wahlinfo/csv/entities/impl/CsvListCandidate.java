package com.tu.wahlinfo.csv.entities.impl;

import java.util.Map;

import com.tu.wahlinfo.csv.entities.CsvAbstractCandidate;

public class CsvListCandidate extends CsvAbstractCandidate {

	private static final String TABLE_NAME = "WIListCandidate";

	private long federalStateId;
	private int partyListRank;

	public CsvListCandidate(long id, String firstname, String surname,
			String yearOfBirth, Long partyId, ElectionYear candidatureYear,
			long federalStateId, String partyListRank) {
		super(id, firstname, surname, yearOfBirth, partyId, candidatureYear);
		this.federalStateId = federalStateId;
		this.partyListRank = Integer.parseInt(partyListRank);
	}

	public long getFederalStateId() {
		return federalStateId;
	}

	public int getPartyListRank() {
		return partyListRank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ (int) (federalStateId ^ (federalStateId >>> 32));
		result = prime * result + partyListRank;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CsvListCandidate other = (CsvListCandidate) obj;
		if (federalStateId != other.federalStateId)
			return false;
		if (partyListRank != other.partyListRank)
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = super.toRelationalStruct();
		res.put("federalStateId", Long.toString(this.federalStateId));
		res.put("rank", Integer.toString(this.partyListRank));
		return res;
	}

	@Override
	public String getTargetTableName() {
		return TABLE_NAME;
	}

}
