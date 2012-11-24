package com.tu.wahlinfo.csv.entities;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.model.Persistable;

public abstract class CsvAbstractCandidate implements Persistable {

	private long id;
	private String firstnames;
	private String surname;
	private int yearOfBirth;
	private Long partyId;
	private ElectionYear candidatureYear;

	public CsvAbstractCandidate(long id, String firstnames, String surname,
			String yearOfBirth, Long partyId, ElectionYear candidatureYear) {
		super();
		this.id= id;
		this.firstnames = firstnames;
		this.surname = surname;
		this.yearOfBirth = Integer.parseInt(yearOfBirth);
		this.partyId = partyId;
		this.candidatureYear = candidatureYear;
	}

	public long getId() {
		return id;
	}

	public String getFirstnames() {
		return firstnames;
	}

	public String getSurname() {
		return surname;
	}

	public int getYearOfBirth() {
		return yearOfBirth;
	}

	public Long getPartyId() {
		return partyId;
	}

	public void setPartyId(long partyId) {
		this.partyId = partyId;
	}

	public String getCandidatureYear() {
		return candidatureYear.toCleanString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CsvAbstractCandidate other = (CsvAbstractCandidate) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("id", Long.toString(this.id));
		res.put("name", this.surname.concat(",").concat(firstnames));
		res.put("electionYear", this.candidatureYear.toCleanString());
		if (partyId != null) {
			res.put("partyId", Long.toString(this.partyId));
		}
		res.put("yearOfBirth", Integer.toString(this.yearOfBirth));
		return res;
	}
}
