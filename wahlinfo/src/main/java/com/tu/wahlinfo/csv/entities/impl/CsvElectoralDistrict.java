package com.tu.wahlinfo.csv.entities.impl;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

public class CsvElectoralDistrict implements Persistable {

	private static final String TABLE_NAME = "WIElectoralDistrict";

	private long electoralDistrictId;
	private String electoralDistrictName;
	private long federalStateId;

	public CsvElectoralDistrict(String electoralDistrictId,
			String electoralDistrictName) {
		this.electoralDistrictId = Long.parseLong(electoralDistrictId);
		this.electoralDistrictName = electoralDistrictName;

	}

	public long getElectoralDistrictId() {
		return electoralDistrictId;
	}

	public String getElectoralDistrictName() {
		return electoralDistrictName;
	}

	public long getFederalStateId() {
		return federalStateId;
	}

	public void setFederalStateId(String federalStateId) {
		this.federalStateId = Long.parseLong(federalStateId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (electoralDistrictId ^ (electoralDistrictId >>> 32));
		result = prime
				* result
				+ ((electoralDistrictName == null) ? 0 : electoralDistrictName
						.hashCode());
		result = prime * result
				+ (int) (federalStateId ^ (federalStateId >>> 32));
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
		CsvElectoralDistrict other = (CsvElectoralDistrict) obj;
		if (electoralDistrictId != other.electoralDistrictId)
			return false;
		if (electoralDistrictName == null) {
			if (other.electoralDistrictName != null)
				return false;
		} else if (!electoralDistrictName.equals(other.electoralDistrictName))
			return false;
		if (federalStateId != other.federalStateId)
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("federalStateId", Long.toString(this.federalStateId));
		res.put("name", this.electoralDistrictName);
		res.put("number", Long.toString(this.electoralDistrictId));
		return res;
	}

	@Override
	public String getTargetTableName() {
		return TABLE_NAME;
	}

}
