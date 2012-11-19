with AllVotes as (
	select	pv.electionId, sum(pv.receivedVotes) as votes
	from 	WIPartyVotes pv
	group by pv.electionId
		)
	select	pv.partyId, pv.electionId
	from	WIPartyVotes pv, AllVotes av
	where	pv.electionId 		= av.electionId		and
		pv.receivedVotes 	< 0.05 * av.votes

