package com.tu.wahlinfo.csv.entities.impl;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

/**
 * 
 * @author cg
 * 
 */
public class CsvElection implements Persistable {

	private static final String TABLE_NAME = "WIElection";

	private ElectionYear year;

	public CsvElection(ElectionYear year) {
		super();
		this.year = year;
	}

	public ElectionYear getYear() {
		return year;
	}

	public void setYear(ElectionYear year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		CsvElection other = (CsvElection) obj;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("electionYear", year.toCleanString());
		return res;
	}

	@Override
	public String getTargetTableName() {
		return TABLE_NAME;
	}

}
