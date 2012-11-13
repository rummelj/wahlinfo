package com.tu.wahlinfo.csv.entities.impl;

public class CsvGeneratedVote {

    private long tempId;
    private long electoralDistrictId;
    private String directVoteParty;
    private String listVoteParty;
    private ElectionYear year;

    /**
     * 
     * @param tempId
     *            The temp id is necessary to avoid equals() collisions of
     *            matching votes.
     * @param electoralDistrictId
     * @param directVoteParty
     * @param listVoteParty
     */
    public CsvGeneratedVote(long tempId, ElectionYear year, long electoralDistrictId, String directVoteParty, String listVoteParty) {
	this.tempId = tempId;
	this.year = year;
	this.electoralDistrictId = electoralDistrictId;
	this.directVoteParty = directVoteParty;
	this.listVoteParty = listVoteParty;
    }

    public long getElectoralDistrictId() {
	return electoralDistrictId;
    }

    public String getDirectVoteParty() {
	return directVoteParty;
    }

    public String getListVoteParty() {
	return listVoteParty;
    }

    public long getTempId() {
	return tempId;
    }

    public String getYear() {
	return year.toCleanString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((directVoteParty == null) ? 0 : directVoteParty.hashCode());
	result = prime * result + (int) (electoralDistrictId ^ (electoralDistrictId >>> 32));
	result = prime * result + ((listVoteParty == null) ? 0 : listVoteParty.hashCode());
	result = prime * result + (int) (tempId ^ (tempId >>> 32));
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
	if (directVoteParty == null) {
	    if (other.directVoteParty != null)
		return false;
	} else if (!directVoteParty.equals(other.directVoteParty))
	    return false;
	if (electoralDistrictId != other.electoralDistrictId)
	    return false;
	if (listVoteParty == null) {
	    if (other.listVoteParty != null)
		return false;
	} else if (!listVoteParty.equals(other.listVoteParty))
	    return false;
	if (tempId != other.tempId)
	    return false;
	if (year != other.year)
	    return false;
	return true;
    }

}
