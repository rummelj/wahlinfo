package com.tu.wahlinfo.csv.entities.impl;

import com.tu.wahlinfo.model.Persistable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cg
 */
public class CsvElectoralDistrictVoteData implements Persistable {

    private static final String TABLE_NAME = "WIElectoralDistrictVoteData";
    private final long electoralDistrictId;
    private final ElectionYear electionYear;
    private final long possibleVotes;
    private final long submittedVotes;
    private final long validDirectVotes;
    private final long validPartyVotes;
    private final long invalidDirectVotes;
    private final long invalidPartyVotes;

    public CsvElectoralDistrictVoteData(String electoralDistrictId, ElectionYear electionYear, String possibleVotes, String submittedVotes, String validDirectVotes,
            String validPartyVotes, String invalidDirectVotes, String invalidPartyVotes) {
        this.electoralDistrictId = Long.parseLong(electoralDistrictId);
        this.electionYear = electionYear;
        this.possibleVotes = Long.parseLong(possibleVotes);
        this.submittedVotes = Long.parseLong(submittedVotes);
        this.validDirectVotes = Long.parseLong(validDirectVotes);
        this.validPartyVotes = Long.parseLong(validPartyVotes);
        this.invalidDirectVotes = Long.parseLong(invalidDirectVotes);
        this.invalidPartyVotes = Long.parseLong(invalidPartyVotes);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.electoralDistrictId ^ (this.electoralDistrictId >>> 32));
        hash = 97 * hash + (this.electionYear != null ? this.electionYear.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CsvElectoralDistrictVoteData other = (CsvElectoralDistrictVoteData) obj;
        if (this.electoralDistrictId != other.electoralDistrictId) {
            return false;
        }
        if (this.electionYear != other.electionYear) {
            return false;
        }
        return true;
    }

    @Override
    public String getTargetTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, String> toRelationalStruct() {
        Map<String, String> res = new HashMap<>();
        res.put("electoralDistrictId", Long.toString(this.electoralDistrictId));
        res.put("electionYear", this.electionYear.toCleanString());
        res.put("possibleVotes", Long.toString(this.possibleVotes));
        res.put("submittedVotes", Long.toString(this.submittedVotes));
        res.put("validDirectVotes", Long.toString(this.validDirectVotes));
        res.put("validPartyVotes", Long.toString(this.validPartyVotes));
        res.put("invalidDirectVotes", Long.toString(this.invalidDirectVotes));
        res.put("invalidPartyVotes", Long.toString(this.invalidPartyVotes));
        return res;
    }

    public long getElectoralDistrictId() {
        return electoralDistrictId;
    }

    public ElectionYear getElectionYear() {
        return electionYear;
    }

    public long getPossibleVotes() {
        return possibleVotes;
    }

    public long getSubmittedVotes() {
        return submittedVotes;
    }

    public long getValidDirectVotes() {
        return validDirectVotes;
    }

    public long getValidPartyVotes() {
        return validPartyVotes;
    }

    public long getInvalidDirectVotes() {
        return invalidDirectVotes;
    }

    public long getInvalidPartyVotes() {
        return invalidPartyVotes;
    }
}
