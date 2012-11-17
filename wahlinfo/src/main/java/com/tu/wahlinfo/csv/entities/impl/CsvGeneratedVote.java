package com.tu.wahlinfo.csv.entities.impl;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

public class CsvGeneratedVote implements Persistable {

    private static final String TABLE_NAME = "WIFilledVotingPaper";
    
    private long tmpId;
    private long electoralDistrictId;
    private long directVotePartyId;
    private long listVotePartyId;
    private ElectionYear year;

    /**
     * 
     * @param tmpId
     *            The temp id is necessary to avoid equals()/ hash-collisions of
     *            matching votes.
     * @param electoralDistrictId
     * @param directVoteParty
     * @param listVoteParty
     */
    public CsvGeneratedVote(long tmpId, ElectionYear year, long electoralDistrictId, long directVotePartyId, long listVotePartyId) {
	this.tmpId = tmpId;
	this.year = year;
	this.electoralDistrictId = electoralDistrictId;
	this.directVotePartyId = directVotePartyId;
	this.listVotePartyId = listVotePartyId;
    }

    public long getElectoralDistrictId() {
	return electoralDistrictId;
    }

    public long getDirectVotePartyId() {
	return directVotePartyId;
    }

    public long getListVotePartyId() {
	return listVotePartyId;
    }

    public long getTmpId() {
	return tmpId;
    }

    public String getYear() {
	return year.toCleanString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (directVotePartyId ^ (directVotePartyId >>> 32));
	result = prime * result + (int) (electoralDistrictId ^ (electoralDistrictId >>> 32));
	result = prime * result + (int) (listVotePartyId ^ (listVotePartyId >>> 32));
	result = prime * result + (int) (tmpId ^ (tmpId >>> 32));
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
	CsvGeneratedVote other = (CsvGeneratedVote) obj;
	if (directVotePartyId != other.directVotePartyId)
	    return false;
	if (electoralDistrictId != other.electoralDistrictId)
	    return false;
	if (listVotePartyId != other.listVotePartyId)
	    return false;
	if (tmpId != other.tmpId)
	    return false;
	if (year != other.year)
	    return false;
	return true;
    }

    @Override
    public Map<String, String> toRelationalStruct() {
	Map<String, String> res = new HashMap<String, String>();
	res.put("id", Long.toString(this.tmpId));
	res.put("electoralDistrictId", Long.toString(this.electoralDistrictId));
	res.put("partyId", Long.toString(this.listVotePartyId));
	res.put("directVotePartyId", Long.toString(this.directVotePartyId));
	res.put("electionYear", this.year.toCleanString());
	return res;
    }

    @Override
    public String getTargetTableName() {
	return TABLE_NAME;
    }
}
