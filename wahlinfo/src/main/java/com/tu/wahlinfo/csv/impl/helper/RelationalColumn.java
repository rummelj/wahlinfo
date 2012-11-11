package com.tu.wahlinfo.csv.impl.helper;

/**
 * Helper enum, to make map key independent from string specifications and refactorable.
 *
 * @author cg
 */
public enum RelationalColumn {

    FS_NAME("FederalStateName"), EL_ID("ElectoralDistrictId"), EL_NAME("ElectoralDistrictName"),
    F_NAME("FirstName"), S_NAME("Surname"), PARTY("Party"), L_RANK("ListRank"), YOB("YearOfBirth"),
    D_Votes("DirectVotes"), L_Votes("ListVotes"), V_ID("VoteID"), D_VOTE_PARTY("DirectVoteParty"), L_VOTE_PARTY("ListVoteParty");
    private String columnString;

    RelationalColumn(String fullSpecification) {
        this.columnString = fullSpecification;
    }

    @Override
    public String toString() {
        return "RelationalColumn{" + "fullSpecification=" + columnString + '}';
    }

    public String toColumnString() {
        return columnString;
    }
}
