create or replace view DirectMandatesView:electionYear as (

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
	)
	
	select  wpd.directCandidateId
	from	WinnerPerDistrict wpd
);


create or replace view ListMandatesView:electionYear as (

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

	-- direct mandates per party and state
	DirectMandatesPerPartyAndState as (
		select	wpd.partyId, ed.federalStateId, count(*) as dirMandates
		from	WinnerPerDistrict wpd, WIElectoralDistrict ed
		where	wpd.electoralDistrictId	= ed.number
		group by wpd.partyId, ed.federalStateId
	),

	-- seats per party and state accounted with direct mandates
	RealSeatsPerPartyAndState as (
		select	spsd.partyId, spsd.federalStateId,	(	spsd.seats - 
									case when (dmpps.dirMandates is null) then 0 else dmpps.dirMandates end
								) as realStateSeats
		from	LowerDistributionView:electionYear spsd left outer join DirectMandatesPerPartyAndState dmpps on
			spsd.partyId 		= dmpps.partyId		and
			spsd.federalStateId	= dmpps.federalStateId			
	),

	-- select only list candidates who do not appear as respective successful direct candidate
	QualifiedListCandidates as (
		select	lc.id, lc.partyId, lc.federalStateId, lc.rank
		from	WIListCandidate lc left outer join WinnerPerDistrict wpd on lc.id = wpd.directCandidateId
		where	wpd.directCandidateId	is null	and
			lc.electionYear		= ':electionYear'
	),

	-- as there are now only valid candidates, we can change their rank in order to comply which the number of candidates who are needed per party and state
	RankAdaptedListCandidates as (
		select	qlc.id, qlc.partyId, qlc.federalStateId, rank() OVER (PARTITION BY qlc.partyId, qlc.federalStateId ORDER BY qlc.rank asc)
		from	QualifiedListCandidates qlc
	),

	-- add list candidates for each party and federal state
	-- exclude double candidates and include following candidates (e.g. 5 and 2 double candidates -> rank <= 7)
	ListMandates as (
		select	ralc.id
		from	RankAdaptedListCandidates ralc, RealSeatsPerPartyAndState rspps		
		where	ralc.partyId		= rspps.partyId						and		
			ralc.federalStateId	= rspps.federalStateId					and										
			ralc.rank		<=	rspps.realStateSeats	
	)
	
	select	lm.id as listCandidateId
	from	ListMandates lm
);