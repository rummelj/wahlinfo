create or replace view LowerDistributionView:electionYear as (

	with WinnerPerDistrict as (
		with MaxVotes as (
			select	dc.electoralDistrictId, max(dc.receivedVotes) as receivedVotes
			from	WIDirectCandidate dc
			where	dc.electionYear	= ':electionYear'
			group by dc.electoralDistrictId
		)
		select	distinct on (dc.electoralDistrictId) dc.electoralDistrictId, dc.id as directCandidateId, dc.partyId
		from	MaxVotes m, WIDirectCandidate dc
		where	m.electoralDistrictId	= dc.electoralDistrictId	and
			m.receivedVotes		= dc.receivedVotes		and
			dc.electionYear		= ':electionYear'
		order by dc.electoralDistrictId, random()
	),

	MaxSeats as (
		select	max(psd.seats) as seats
		from	UpperDistributionView:electionYear psd		
	),

	-- the algorithm produces as many entries per iteration as there states a party has received votes in
	-- in the worst case, all mandates of the party having the most ones are assigned to a single state 
	-- therefore (maxSeats) many iterations are required 
	IterationDivisors as (
		select	d.value
		from	WIDivisor d
		where	d.id <= 	(	select	seats from MaxSeats)
	),

	-- iterations are realized as a "controlled" cross product
	RankValues as (
		select	psd.partyId, pv.federalStateId, (cast(pv.receivedVotes as float) / d.value) as rankValue
		from	UpperDistributionView:electionYear psd, WIPartyVotes pv, IterationDivisors d
		where	psd.partyId	= pv.partyId
			
	),

	-- calculate numerical ranks in order to be able to extract only the top required ranks per party (does not work with limit)
	RankedRankValues as (
		select	rv.partyId, rv.federalStateId, rv.rankValue, rank() OVER (PARTITION BY rv.partyId ORDER BY rv.rankValue DESC)
		from	RankValues rv
	),

	-- per party only use top n entries as read from UpperDistributionView:electionYear and aggregate them per party and state
	-- the number of seats 
	NominalSeatsPerPartyAndState as (
		select	rrv.partyId, rrv.federalStateId, count(*) as nomStateSeats
		from	RankedRankValues rrv
		where 	rrv.rank	<=	(	select	psd.seats
							from 	UpperDistributionView:electionYear psd
							where	rrv.partyId		= psd.partyId							
								)	
		group by rrv.partyId, rrv.federalStateId
		order by rrv.partyId
	)
	
	select	nspps.partyId, nspps.federalStateId, nspps.nomStateSeats as seats
	from	NominalSeatsPerPartyAndState nspps
);