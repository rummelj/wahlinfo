package com.tu.wahlinfo.queries;

public interface Queries {
    
    public static final String WinnerPerDistrict =
	    
	    "with MostVotes as (" +
		    "select dv.electoralDistrictId, max(dv.reveivedVotes) as max, dv.electionId " +
		    "from WIDirectVote dv " +
		    "group by dv.electoralDistrictId, dv.electionId " +
		    		") " +
		    
	    "select mv.electoralDistrictId, dv.directCandidateId " +
	    "from WIDirectVote dv, MostVotes mv" +
	    "where	dv.electoralDistrictId = mv.electoralDistrictId and " +
	    		"dv.receivedVotes = mv.max and " +
	    		"dv.electionId = mv.electionId" +
	    "";      
    
    public static final String PartiesBelow5Percent = 
	    
	    "with AllVotes as ( " +
		    "select pv.electionId, sum(pv.reveivedVotes) as votes" +
		    "from WIPartyVotes pv " +
		    "group by pv.electionId " +
		    		") " +	   
	    "select pv.partyId, pv.electionId " +				
	    "from WIPartyVotes pv, AllVotes av " +
	    "where	pv.electionId = av.electionId and " +
	    		"pv.receivedVotes < 0.05 * av.votes " +
	    "";
    
    public static final String DirectCandidatesWithoutListOnes = 
	    
	    "with opposite as (	select dc.id, dc.electionId" +
		    		"from WIDirectCandidate dc, WIElectoralDistrict ed, WIListCandidate lc " +
		    		"where	dc.electoralDistrictId = ed.number and " +
		    			"ed.federalStateId = lc.federalStateId and " +
		    			"dc.partyId = lc.partyId and" +
		    			"dc.electionId = lc.electionId" +
		    		") " +		    		
	    "select dc.id, dc.lectionId " +
	    "from WIDirectCandidate dc left join opposite o on dc.id = o.id and dc.electionId = o.electionId " +
	    "where o.id is null" +
	    "";
    
    public static final String AvailableSeats =
	    
	    "select 598 - count(dc.id), dc.electionId" +
	    "from WinnerPerDistrict wpd, WIDirectCandidate dc " +
	    "where 	wpd.directCandidateId = dc.id and " +		
	    		"wpd.electionId = dc.electionId and " +
	    		"(" +		 
	    		"dc.partyId is null or " +							
	    		"dc.id in (	select d.id " +		 // no party list in federal state
	    				"from DirectCandidatesWithoutListOnes d " +
	    				"where d.electionId = dc.electionId) or " +	   	
	    		"dc.partyId in (	select p.partyId " +
	    					"from PartiesBelow5Percent " +
	    					"where p.electionId = dc.electionId) " +
	    		")" +
	    "";             
    
    public static final String relevantVotingPapersPerParty =
	    
	    "select p.name, count(fvp.id)" +
	    "from FilledVotingPaper fvp, WinnerPerDistrict wpd, WIDirectCandidate dc, WIParty p" +
	    "where 	fvp.electionId = wpd.electionId and " +
	    		"wpd.electionId = dc.electionId and " +	    		
	    		"wpd.directCandidateId = dc.id and " +	    		
	    		"fvp.directCandidateId = wpd.directCandidateId and " +
	    		"dc.partyId = p.partyId and " +
	    		"not ( dc.partyId is null) and " +					// ignore first vote to successful partyless.
	    		"not in (	select d.id " +		 				// ignore first vote to party without list 
	    				"from DirectCandidatesWithoutListOnes d " +		//candidates in same federal state.
	    				"where d.electionId = dc.electionId) " +				
	    "group by p.name" +
	    "";
		    

}
