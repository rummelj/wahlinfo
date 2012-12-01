package com.tu.wahlinfo.csv.entities.impl;

import java.util.HashMap;
import java.util.Map;

import com.tu.wahlinfo.model.Persistable;

public class CsvGeneratedVote implements Persistable {

	private static final String TABLE_NAME = "WIFilledVotingPaper";

	private long tmpId;
	private long electoralDistrictId;
	private Long listVotePartyId;
	private Long directVoteCandidateId;

	/**
	 * 
	 * @param tmpId
	 *            The temp id is necessary to avoid equals()/ hash-collisions of
	 *            matching votes.
	 * @param electoralDistrictId
	 * @param directVoteParty
	 * @param listVoteParty
	 */
	public CsvGeneratedVote(long tmpId, long electoralDistrictId,
			Long directVoteCandidateId, Long listVotePartyId) {
		this.tmpId = tmpId;
		this.electoralDistrictId = electoralDistrictId;
		this.directVoteCandidateId = directVoteCandidateId;
		this.listVotePartyId = listVotePartyId;
	}

	public long getElectoralDistrictId() {
		return electoralDistrictId;
	}

	public Long getDirectVoteCandidateId() {
		return directVoteCandidateId;
	}

	public long getListVotePartyId() {
		return listVotePartyId;
	}

	public long getTmpId() {
		return tmpId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((directVoteCandidateId == null) ? 0 : directVoteCandidateId
						.hashCode());
		result = prime * result
				+ (int) (electoralDistrictId ^ (electoralDistrictId >>> 32));
		result = prime * result
				+ ((listVotePartyId == null) ? 0 : listVotePartyId.hashCode());
		result = prime * result + (int) (tmpId ^ (tmpId >>> 32));
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
		if (directVoteCandidateId == null) {
			if (other.directVoteCandidateId != null)
				return false;
		} else if (!directVoteCandidateId.equals(other.directVoteCandidateId))
			return false;
		if (electoralDistrictId != other.electoralDistrictId)
			return false;
		if (listVotePartyId == null) {
			if (other.listVotePartyId != null)
				return false;
		} else if (!listVotePartyId.equals(other.listVotePartyId))
			return false;
		if (tmpId != other.tmpId)
			return false;
		return true;
	}

	@Override
	public Map<String, String> toRelationalStruct() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("id", Long.toString(this.tmpId));
		res.put("electoralDistrictId", Long.toString(this.electoralDistrictId));

		if (this.listVotePartyId != null) {
			res.put("partyId", Long.toString(this.listVotePartyId));
		}

		if (this.directVoteCandidateId != null) {
			res.put("directCandidateId",
					Long.toString(this.directVoteCandidateId));
		}
		return res;
	}

	@Override
	public String getTargetTableName() {
		return TABLE_NAME;
	}
}
