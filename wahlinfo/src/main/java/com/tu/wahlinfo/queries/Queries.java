package com.tu.wahlinfo.queries;

public interface Queries {
   
    

      

    public static final String relevantVotingPapersPerParty =

    "select p.name, count(fvp.id)" + "from FilledVotingPaper fvp, WinnerPerDistrict wpd, WIDirectCandidate dc, WIParty p"
	    + "where 	fvp.electionId = wpd.electionId and " + "wpd.electionId = dc.electionId and " + "wpd.directCandidateId = dc.id and "
	    + "fvp.directCandidateId = wpd.directCandidateId and " + "dc.partyId = p.partyId and " + "not ( dc.partyId is null) and " + // ignore
																	// first
																	// vote
																	// to
																	// successful
																	// partyless.
	    "not in (	select d.id " + // ignore first vote to party without list
	    "from DirectCandidatesWithoutListOnes d " + // candidates in same
							// federal state.
	    "where d.electionId = dc.electionId) " + "group by p.name" + "";

}
