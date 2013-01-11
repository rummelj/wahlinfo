BEGIN

insert  into WIFilledVotingPaper(electoralDistrictId, directCandidateId, partyId) 
        values (electoralDistrictId, :directCandidateId, :partyId);

update  WIDirectCandidate 
set     receivedVotes = receivedVotes + 1
where   id = :directCandidateId;

update  WIPartyVotes
set     receivedVotes = receivedVotes +1
where   partyId = :partyId;

-- CHECK: Update electoral distrcict vote data? (submitted votes, invalid...)