package com.tu.wahlinfo.csv.entities.impl;

/**
 * "In-the-middle" class. Thus not conceptualized for persistence.
 * 
 * @author cg
 * 
 */
public class CsvPartialVoteAggregation {

	private Long partyId2005;
	private Long partyId2009;
	private Long directCanidateId2005;
	private Long directCanidateId2009;
	private long numDirectVotes2005;
	private long numDirectVotes2009;
	private long numListVotes2005;
	private long numListVotes2009;

	public CsvPartialVoteAggregation(Long directCanidateId2005,
			Long directCanidateId2009, Long partyId2005, Long partyId2009,
			String numDirectVotes2005, String numDirectVotes2009,
			String numListVotes2005, String numListVotes2009) {
		this.directCanidateId2005 = directCanidateId2005;
		this.directCanidateId2009 = directCanidateId2009;
		this.partyId2005 = partyId2005;
		this.partyId2009 = partyId2009;
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

	public Long getPartyId(ElectionYear year) {
		if (year.equals(ElectionYear._2005)) {
			return partyId2005;
		} else {
			return partyId2009;
		}
	}

	public long getNumListVotes(ElectionYear year) {
		if (year.equals(ElectionYear._2005)) {
			return numListVotes2005;
		} else {
			return numListVotes2009;
		}
	}

	public Long getDirectCanidateId(ElectionYear year) {
		if (year.equals(ElectionYear._2005)) {
			return directCanidateId2005;
		} else {
			return directCanidateId2009;
		}
	}

	public Long getDirectCanidateId2005() {
		return directCanidateId2005;
	}

	public Long getDirectCanidateId2009() {
		return directCanidateId2009;
	}

	public Long getPartyId2005() {
		return partyId2005;
	}

	public Long getPartyId2009() {
		return partyId2009;
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
		result = prime
				* result
				+ ((directCanidateId2005 == null) ? 0 : directCanidateId2005
						.hashCode());
		result = prime
				* result
				+ ((directCanidateId2009 == null) ? 0 : directCanidateId2009
						.hashCode());
		result = prime * result
				+ (int) (numDirectVotes2005 ^ (numDirectVotes2005 >>> 32));
		result = prime * result
				+ (int) (numDirectVotes2009 ^ (numDirectVotes2009 >>> 32));
		result = prime * result
				+ (int) (numListVotes2005 ^ (numListVotes2005 >>> 32));
		result = prime * result
				+ (int) (numListVotes2009 ^ (numListVotes2009 >>> 32));
		result = prime * result
				+ ((partyId2005 == null) ? 0 : partyId2005.hashCode());
		result = prime * result
				+ ((partyId2009 == null) ? 0 : partyId2009.hashCode());
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
		if (directCanidateId2005 == null) {
			if (other.directCanidateId2005 != null)
				return false;
		} else if (!directCanidateId2005.equals(other.directCanidateId2005))
			return false;
		if (directCanidateId2009 == null) {
			if (other.directCanidateId2009 != null)
				return false;
		} else if (!directCanidateId2009.equals(other.directCanidateId2009))
			return false;
		if (numDirectVotes2005 != other.numDirectVotes2005)
			return false;
		if (numDirectVotes2009 != other.numDirectVotes2009)
			return false;
		if (numListVotes2005 != other.numListVotes2005)
			return false;
		if (numListVotes2009 != other.numListVotes2009)
			return false;
		if (partyId2005 == null) {
			if (other.partyId2005 != null)
				return false;
		} else if (!partyId2005.equals(other.partyId2005))
			return false;
		if (partyId2009 == null) {
			if (other.partyId2009 != null)
				return false;
		} else if (!partyId2009.equals(other.partyId2009))
			return false;
		return true;
	}

}
