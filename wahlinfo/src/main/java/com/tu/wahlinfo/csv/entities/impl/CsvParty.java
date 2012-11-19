package com.tu.wahlinfo.csv.entities.impl;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

/**
 * 
 * @author cg
 * 
 */
public class CsvParty implements Persistable{

    private static final String TABLE_NAME ="WIParty";
    
    private long partyId;
    private String partyName;

    public CsvParty(long partyId, String partyName) {
	super();
	this.partyId = partyId;
	this.partyName = partyName;
    }

    public long getPartyId() {
	return partyId;
    }

    public String getPartyName() {
	return partyName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (partyId ^ (partyId >>> 32));
	result = prime * result + ((partyName == null) ? 0 : partyName.hashCode());
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
	CsvParty other = (CsvParty) obj;
	if (partyId != other.partyId)
	    return false;
	if (partyName == null) {
	    if (other.partyName != null)
		return false;
	} else if (!partyName.equals(other.partyName))
	    return false;
	return true;
    }

    @Override
    public Map<String, String> toRelationalStruct() {
	Map<String, String> res = new HashMap<String, String>();	
	res.put("id", Long.toString(this.partyId));
	res.put("name", this.partyName);
	return res;
    }

    @Override
    public String getTargetTableName() {
	return TABLE_NAME;
    }
    

}
