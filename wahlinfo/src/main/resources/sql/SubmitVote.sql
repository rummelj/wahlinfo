BEGIN;

insert  into WIFilledVotingPaper(electoralDistrictId, directCandidateId, partyId) 
        values (:electoralDistrictId, :directCandidateId, :partyId);

update  WIDirectCandidate 
set     receivedVotes = receivedVotes + 1
where   id = :directCandidateId;

update  WIPartyVotes
set     receivedVotes = receivedVotes +1
where   partyId = :partyId;

update  WIElectoralDistrictVoteData
set     submittedVotes = submittedVotes +1
where   electionYear = '2009' and
        electoralDistrictId = :electoralDistrictId;

update  WIElectoralDistrictVoteData
set     :icvalidDirectVotes = :icvalidDirectVotes +1
where   electionYear = '2009' and
        electoralDistrictId = :electoralDistrictId;

update  WIElectoralDistrictVoteData
set     :ipvalidPartyVotes = :ipvalidPartyVotes +1
where   electionYear = '2009' and
        electoralDistrictId = :electoralDistrictId;

COMMIT;