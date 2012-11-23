with Prep as (
	with Seats as (
		select	psd.seats
		from	WIPartySeatDistribution psd
		where	psd.electionId	= :electionId	and
			psd.partyId	= :partyId
	),
	FederalStates as (
		select	count(*) as amount
		from	WIPartyVotes pv, WIParty p
		where	pv.partyId	= p.id	and
			p.electionId 	= :electionId	and
			p.id		= :partyId	
	)
	select s.seats, ceil (s.seats::float / fs.amount) as iterations
	from Seats s, FederalStates fs
),

Ranking as (
	select	pv.federalStateId, (pv.receivedVotes::float / d.value) as rvalue
	from	WIPartyVotes pv, WIParty p, WIDivisor d
	where	pv.partyId	= p.id				and
		p.electionId	= :electionId				and
		p.id		= :partyId				and
		d.id <= ( select p.iterations from Prep p )	
	order by rvalue desc
	limit	( select p1.seats from Prep p1 )
),

AllMandatesPerFederalState as (
	select r.federalStateId, count(*) as mandates
	from Ranking r		
	group by r.federalStateId
),

DirectMandatesPerFederalState as (	
	select 	ed.federalStateId, count(*) as mandates
	from 	WinnerPerDistrict wpd, WIElectoralDistrict ed
	where	wpd.electoralDistrictId	= ed.number	and
		wpd.partyId		= :partyId
	group by ed.federalStateId
)

insert 	into WIListMandateDistribution(listCandidateId, electionId)
select	lc.id, :electionId
from	AllMandatesPerFederalState am, DirectMandatesPerFederalState dm, WIListCandidate lc
where	am.federalStateId	= dm.federalStateId	and
	dm.federalStateId	= lc.federalStateId	and
	lc.electionId		= :electionId			and
	lc.partyId		= :partyId			and
	lc.rank			<= ( am.mandates - dm.mandates)
;

insert	into WIDirectMandateDistribution(directCandidateId, electionId)
select  wpd.directCandidateId, :electionId
from	WinnerPerDistrict wpd
;