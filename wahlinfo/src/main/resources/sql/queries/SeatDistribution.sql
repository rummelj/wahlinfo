with WinnerPerDistrict as (
	with MaxVotes as (
		select	dc.electionID, dc.electoralDistrictId, max(dc.receivedVotes) as receviedVotes
		from	WIDirectCandidate dc
		group by dc.electionId, dc.electoralDistrictId
	)
	select	distinct on (dc.electionId, dc.electoralDistrictId) dc.electionId, dc.electoralDistrictId, dc.id as directCandidateId, dc.partyId
	from	MaxVotes m, WIDirectCandidate dc
	where	m.electionId	= dc.electionId				and
		m.electoralDistrictId	= dc.electoralDistrictId	and
		m.receivedVotes		= dc.receivedVotes
	group by dc.electionId, dc.electoralDistrictId, random()
),

DirectVoteWinnerWithoutListOrIndependent as (
	select	wpd.directCandidateId
	from	WinnerPerDistrict wpd
	where	wpd.partyId is null	or
		not exists 	(	select
					from	WIListCandidate lc, ElectoralDistrict ed
					where	wpd.electionId	= lc.electionId			and
						wpd.partyId	= lc.partyId			and
						wpd.electoralDistrictId	= ed.number		and
						ed.federalStateId	= lc.federalStateID
				)
		)
),

PartiesBelowFivePercent as (
	with FivePercentBarrier as (
		select	pv.electionId, 0.05 * sum(pv.receivedVotes) as minVotes
		from	WIPartyVotes pv
		group by pv.electionId
	)
	select	pv.id
	from	WIPartyVotes pv, FivePercentBarrier fpb
	where	pv.electionId		= fpb.electionId	and
		pv.receivedVotes	< fpb.minVotes
),

AvailableSeats as (
	select 	wpd.electionId, 598 - count(*)
	from	WinnerPerDistrict wpd
	where	wpd.partyId		not in	(	select 	*
							from 	PartiesBelowFivePercent
						)	and
		wpd.directCandidateId	not in	(	select	*
							from	DirectVoteWinnerWithoutListOrIndependent d
						)
	group by wpd.electionId
),

QualifiedVotes as (
	select	fvp.partyId, sum (fvp.id)
	from	WIFilledVotingPaper fvp
	where	fvp.directCandidateId	not in	(	select	*
							from	DirectVoteWinnerWithoutListOrIndependent
						)
	group by fvp.partyId
)

	