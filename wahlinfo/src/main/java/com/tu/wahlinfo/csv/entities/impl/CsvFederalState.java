package com.tu.wahlinfo.csv.entities.impl;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

public class CsvFederalState implements Persistable {

	private static final String TABLE_NAME = "WIFederalState";

	private String name;
	private long federalStateId;

	public CsvFederalState(String name, String federalStateId) {
		this.name = name;
		this.federalStateId = Long.parseLong(federalStateId);
	}

	public String getName() {
		return name;
	}

	public long getFederalStateId() {
		return federalStateId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (federalStateId ^ (federalStateId >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CsvFederalState other = (CsvFederalState) obj;
		if (federalStateId != other.federalStateId)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("federalStateId", Long.toString(this.federalStateId));
		res.put("name", this.name);
		return res;
	}

	@Override
	public String getTargetTableName() {
		return TABLE_NAME;
	}

}
