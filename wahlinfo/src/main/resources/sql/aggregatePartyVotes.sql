TRUNCATE WIPartyVotes; 

INSERT INTO	WIPartyVotes (federalStateId, partyId, receivedVotes)

SELECT	ed.federalStateId, fvp.partyId, count(*) as receivedVotes
FROM 	WIFilledVotingPaper fvp, WIElectoralDistrict ed
WHERE	fvp.electoralDistrictId	= ed.number	AND
	fvp.partyId		is not null
GROUP BY ed.federalStateId, fvp.partyId
;