package com.tu.wahlinfo.csv.entities;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.model.Persistable;

public abstract class CsvAbstractCandidate implements Persistable {

	private String firstnames;
	private String surname;
	private int yearOfBirth;
	private String party;
	private ElectionYear candidatureYear;

	public CsvAbstractCandidate(String firstnames, String surname,
			String yearOfBirth, String party, ElectionYear candidatureYear) {
		super();
		this.firstnames = firstnames;
		this.surname = surname;
		this.yearOfBirth = Integer.parseInt(yearOfBirth);
		this.party = party;
		this.candidatureYear = candidatureYear;
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

	public String getParty() {
		return party;
	}

	public String getCandidatureYear() {
		return candidatureYear.toCleanString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((candidatureYear == null) ? 0 : candidatureYear.hashCode());
		result = prime * result
				+ ((firstnames == null) ? 0 : firstnames.hashCode());
		result = prime * result + ((party == null) ? 0 : party.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		result = prime * result + yearOfBirth;
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
		if (candidatureYear != other.candidatureYear)
			return false;
		if (firstnames == null) {
			if (other.firstnames != null)
				return false;
		} else if (!firstnames.equals(other.firstnames))
			return false;
		if (party == null) {
			if (other.party != null)
				return false;
		} else if (!party.equals(other.party))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		if (yearOfBirth != other.yearOfBirth)
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("firstnames", this.firstnames);
		res.put("surname", this.surname);
		res.put("candidatureYear", this.candidatureYear.toCleanString());
		res.put("party", this.party);
		res.put("yearOfBirth", Integer.toString(this.yearOfBirth));
		return res;
	}

}
