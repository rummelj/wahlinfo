delete 
from	WIStatePartySeatDistribution spsd
where	spsd.electionYear	= :electionYear;

delete
from	WIListMandateDistribution lmd
where	lmd.electionYear	= :electionYear;

delete	
from	WIDirectMandateDistribution dmd
where	dmd.electionYear	= :electionYear;

create or replace view WinnerPerDistrict as (
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
);

with MaxSeats as (
	select	max(psd.seats) as seats
	from	WIPartySeatDistribution psd
	where	psd.electionYear	= :electionYear
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
	from	WIPartySeatDistribution psd, WIPartyVotes pv, IterationDivisors d
	where	psd.partyId	= pv.partyId
		
),

-- calculate numerical ranks in order to be able to extract only the top required ranks per party (does not work with limit)
RankedRankValues as (
	select	rv.partyId, rv.federalStateId, rv.rankValue, rank() OVER (PARTITION BY rv.partyId ORDER BY rv.rankValue DESC)
	from	RankValues rv
),

-- per party only use top n entries as read from WIPartySeatDistribution and aggregate them per party and state
-- the number of seats 
NominalSeatsPerPartyAndState as (
	select	rrv.partyId, rrv.federalStateId, count(*) as nomStateSeats
	from	RankedRankValues rrv
	where 	rrv.rank	<=	(	select	psd.seats
						from 	WIPartySeatDistribution psd
						where	rrv.partyId		= psd.partyId	and
							psd.electionYear	= :electionYear
					)	
	group by rrv.partyId, rrv.federalStateId
	order by rrv.partyId
)

insert into WIStatePartySeatDistribution (partyId, federalStateId, seats, electionYear)
select	nspps.*, :electionYear
from	NominalSeatsPerPartyAndState nspps
;

-- direct mandates per party and state
with DirectMandatesPerPartyAndState as (
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
	from	WIStatePartySeatDistribution spsd left outer join DirectMandatesPerPartyAndState dmpps on
		spsd.partyId 		= dmpps.partyId		and
		spsd.federalStateId	= dmpps.federalStateId	and
		spsd.electionYear	= :electionYear
),

-- select only list candidates who do not appear as respective successful direct candidate
QualifiedListCandidates as (
	select	lc.id, lc.partyId, lc.federalStateId, lc.rank
	from	WIListCandidate lc left outer join WinnerPerDistrict wpd on lc.id = wpd.directCandidateId
	where	wpd.directCandidateId	is null	and
		lc.electionYear		= :electionYear
),

-- as there are now only valid candidates, we can change their rank in order to comply which the number of candidates who are needed per party and state
RankAdaptedListCandidates as (
	select	qlc.id, qlc.partyId, qlc.federalStateId, rank() OVER (PARTITION BY qlc.partyId, qlc.federalStateId ORDER BY qlc.rank asc)
	from	QualifiedListCandidates qlc
),

-- add list candidates for each party and federal state
-- exclude double candidates and include following candidates (e.g. 5 and 2 double candidates -> rank <= 7)
ListMandates as (
	select	ralc.id, :electionYear as electionYear
	from	RankAdaptedListCandidates ralc, RealSeatsPerPartyAndState rspps		
	where	ralc.partyId		= rspps.partyId						and		
		ralc.federalStateId	= rspps.federalStateId					and										
		ralc.rank		<=	rspps.realStateSeats	
)

insert 	into WIListMandateDistribution(listCandidateId, electionYear)
	select	lm.id, lm.electionYear
	from	ListMandates lm
;

insert	into WIDirectMandateDistribution(directCandidateId, electionYear)
select  wpd.directCandidateId, :electionYear
from	WinnerPerDistrict wpd
;