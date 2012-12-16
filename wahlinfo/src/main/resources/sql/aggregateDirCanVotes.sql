WITH VotesPerCandidate as (
	SELECT	fvp.directCandidateId, count(*) as receivedVotes
	FROM	WIFilledVotingPaper fvp
	GROUP BY fvp.directCandidateId
)
UPDATE 	WIDirectCandidate dc
SET	receivedVotes = vpc.receivedVotes
FROM	VotesPerCandidate vpc	
INNER JOIN  WIDirectCandidate dc1
ON	dc1.id 	= vpc.directCandidateId
WHERE	dc.id	= dc1.id