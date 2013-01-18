BEGIN

insert  into WIFilledVotingPaper(electoralDistrictId, directCandidateId, partyId) 
        values (:electoralDistrictId, :directCandidateId, :partyId);

update  WIDirectCandidate 
set     receivedVotes = receivedVotes + 1
where   id = :directCandidateId;

update  WIPartyVotes
set     receivedVotes = receivedVotes +1
where   partyId = :partyId;

update  ElectoralDistrictVoteData
set     submittedVotes = submittedVotes +1
where   electionYear = '2009' and
        electoralDistrictId = :electoralDistrictId;

update  ElectoralDistrictVoteData
set     :icvalidDirectVotes = :icvalidDirectVotes +1
where   electionYear = '2009' and
        electoralDistrictId = :electoralDistrictId;

update  ElectoralDistrictVoteData
set     :ipvalidPartyVotes = :ipvalidPartyVotes +1
where   electionYear = '2009' and
        electoralDistrictId = :electoralDistrictId;


COMMIT;