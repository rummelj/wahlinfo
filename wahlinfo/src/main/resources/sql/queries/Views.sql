with WinnerPerDistrict as (
	with MaxVotes as (
		select	dc.electoralDistrictId, max(dc.receivedVotes) as receivedVotes
		from	WIDirectCandidate dc
		where	dc.electionYear	= :electionYear
		group by dc.electoralDistrictId
	)
	select	distinct on (dc.electoralDistrictId) dc.electoralDistrictId, dc.id as directCandidateId, dc.partyId
	from	MaxVotes m, WIDirectCandidate dc
	where	m.electoralDistrictId	= dc.electoralDistrictId	and
		m.receivedVotes		= dc.receivedVotes		and
		dc.electionYear		= :electionYear
	order by dc.electoralDistrictId, random()
)