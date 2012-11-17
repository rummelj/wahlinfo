package com.tu.wahlinfo.csv.entities.impl;

import java.util.Collection;
import java.util.HashSet;

/**
 *  "In-the-middle" class. Thus not conceptualized for persistence.
 * @author cg
 *
 */
public class CsvVoteAggregation {

    private long electoralDistrictId;
    private Collection<CsvPartialVoteAggregation> partialAggregations = new HashSet<CsvPartialVoteAggregation>();

    public CsvVoteAggregation(String electoralDistrictId) {
	this.electoralDistrictId = Long.parseLong(electoralDistrictId);

    }

    public void addPartialVoteAggregation(long partyId, String numDirectVotes2005, String numDirectVotes2009, String numListVotes2005,
	    String numListVotes2009) {
	this.partialAggregations.add(new CsvPartialVoteAggregation(partyId, numDirectVotes2005, numDirectVotes2009, numListVotes2005,
		numListVotes2009));
    }

    public long getElectoralDistrictId() {
	return electoralDistrictId;
    }

    public Collection<CsvPartialVoteAggregation> getPartialAggregations() {
	return partialAggregations;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (electoralDistrictId ^ (electoralDistrictId >>> 32));
	result = prime * result + ((partialAggregations == null) ? 0 : partialAggregations.hashCode());
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
	CsvVoteAggregation other = (CsvVoteAggregation) obj;
	if (electoralDistrictId != other.electoralDistrictId)
	    return false;
	if (partialAggregations == null) {
	    if (other.partialAggregations != null)
		return false;
	} else if (!partialAggregations.equals(other.partialAggregations))
	    return false;
	return true;
    }

}
