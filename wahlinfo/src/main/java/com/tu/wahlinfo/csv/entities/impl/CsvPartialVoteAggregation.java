package com.tu.wahlinfo.csv.entities.impl;

/**
 * "In-the-middle" class. Thus not conceptualized for persistence.
 * @author cg
 *
 */
public class CsvPartialVoteAggregation {

    private String partyName;
    private long numDirectVotes2005;
    private long numDirectVotes2009;
    private long numListVotes2005;
    private long numListVotes2009;

    public CsvPartialVoteAggregation(String partyName, String numDirectVotes2005, String numDirectVotes2009, String numListVotes2005,
	    String numListVotes2009) {
	this.partyName = partyName;
	this.numDirectVotes2005 = parseValue(numDirectVotes2005);
	this.numDirectVotes2009 = parseValue(numDirectVotes2009);
	this.numListVotes2005 = parseValue(numListVotes2005);
	this.numListVotes2009 = parseValue(numListVotes2009);
    }

    private Long parseValue(String value) {
	if (value.equals("")) {
	    return 0L;
	} else {
	    return Long.parseLong(value);
	}
    }

    public long getNumDirectVotes(ElectionYear year) {
	if (year.equals(ElectionYear._2005)) {
	    return numDirectVotes2005;
	} else {
	    return numDirectVotes2009;
	}

    }

    public long getNumListVotes(ElectionYear year) {
	if (year.equals(ElectionYear._2005)) {
	    return numListVotes2005;
	} else {
	    return numListVotes2009;
	}
    }

    public String getPartyName() {
	return partyName;
    }

    public long getNumDirectVotes2005() {
	return numDirectVotes2005;
    }

    public long getNumDirectVotes2009() {
	return numDirectVotes2009;
    }

    public long getNumListVotes2005() {
	return numListVotes2005;
    }

    public long getNumListVotes2009() {
	return numListVotes2009;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (numDirectVotes2005 ^ (numDirectVotes2005 >>> 32));
	result = prime * result + (int) (numDirectVotes2009 ^ (numDirectVotes2009 >>> 32));
	result = prime * result + (int) (numListVotes2005 ^ (numListVotes2005 >>> 32));
	result = prime * result + (int) (numListVotes2009 ^ (numListVotes2009 >>> 32));
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
	CsvPartialVoteAggregation other = (CsvPartialVoteAggregation) obj;
	if (numDirectVotes2005 != other.numDirectVotes2005)
	    return false;
	if (numDirectVotes2009 != other.numDirectVotes2009)
	    return false;
	if (numListVotes2005 != other.numListVotes2005)
	    return false;
	if (numListVotes2009 != other.numListVotes2009)
	    return false;
	if (partyName == null) {
	    if (other.partyName != null)
		return false;
	} else if (!partyName.equals(other.partyName))
	    return false;
	return true;
    }

}
