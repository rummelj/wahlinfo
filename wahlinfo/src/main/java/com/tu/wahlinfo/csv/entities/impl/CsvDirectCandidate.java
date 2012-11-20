package com.tu.wahlinfo.csv.entities.impl;

import java.util.Map;

import com.tu.wahlinfo.csv.entities.CsvAbstractCandidate;

public class CsvDirectCandidate extends CsvAbstractCandidate {

    private final static String TABLE_NAME = "WIDirectCandidate";
    
    private long electoralDistrictId;

    public CsvDirectCandidate(String firstname, String surname, String yearOfBirth, Long partyId, ElectionYear candidatureYear,
	    String electoralDistrictId) {
	super(firstname, surname, yearOfBirth, partyId, candidatureYear);
	this.electoralDistrictId = Long.parseLong(electoralDistrictId);
    }

    public long getElectoralDistrictId() {
	return electoralDistrictId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + (int) (electoralDistrictId ^ (electoralDistrictId >>> 32));
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
	CsvDirectCandidate other = (CsvDirectCandidate) obj;
	if (electoralDistrictId != other.electoralDistrictId)
	    return false;
	return true;
    }

    @Override
    public Map<String, String> toRelationalStruct() {
	Map<String, String> res = super.toRelationalStruct();
	res.put("electoralDistrictId", Long.toString(this.electoralDistrictId));
	return res;
    }

    @Override
    public String getTargetTableName() {
	return TABLE_NAME;
    }

}
