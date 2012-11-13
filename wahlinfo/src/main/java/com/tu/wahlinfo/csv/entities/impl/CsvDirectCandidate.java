package com.tu.wahlinfo.csv.entities.impl;

import com.tu.wahlinfo.csv.entities.CsvAbstractCandidate;

public class CsvDirectCandidate extends CsvAbstractCandidate {

    private long electoralDistrictId;

    public CsvDirectCandidate(String firstname, String surname, String yearOfBirth, String party, ElectionYear candidatureYear,
	    String electoralDistrictId) {
	super(firstname, surname, yearOfBirth, party, candidatureYear);
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

}
