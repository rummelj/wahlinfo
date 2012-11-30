VIEWS
with MaxSeats as (
	select	max(psd.seats) as seats
	from	WIPartySeatDistribution psd
	where	psd.electionId	= :election
),

-- calculate min number of states a party having acquired seats has received votes in
MinOccurences as (
	select min(tmp.num) as occs
	from	(	select	psd.party, count(*) as num
			from	WIPartySeatDistribution psd, WIPartyVotes pv
			where	psd.partyId	= pv.id	and
				psd.electionId	= :electionId
			group by psd.partyId
		) as tmp
),

-- the algorithm produces as many entries per iteration as there states a party has received votes in
-- it terminates as soons as more or equals as many entires are produced as the party has seats
-- therefore the worst case runtime is ceil(maxSeats / minOccurences) (e.g. 20 seats and 1 occ -> 20 iterations)
IterationDivisors as (
	select	d.value
	from	WIDivisor d
	wher	d.id <= ceil	(	select	ms.seats::float / mo.occs
					from 	MaxSeats ms, MinOccurences mo
				)
),

-- iterations are realized as a "controlled" cross product
RankValues as (
	select	psd.partyId, pv.federalStateId, (pv.receivedVotes:float / d.value) as rankValue
	from	WIPartySeatDistribution psd, WIPartyVotes pv, IterationDivisors d
	where	psd.partyId	= pvid
),

-- for each party, select only as many rank values as there seats in order for that party (-> subquery)
SeatsPerPartyAndFederalState as (
	select	rv.partyId, pv.federalStateId, count(*) as seats
	from	RankValues rv
	where	rv.rankValue	in	(	select
						from	RankValues rv1
						where	rv.partyId	= rv1.partyId
						order by rv1.rankValue desc
						limit	(	select
								from	WIPartySeatDistribution psd
								where	rv1.partyId	= psd.partyId
							)
					)
	group by rv.partyId, rv.federalStateId
),

SuccessfulDoubleCandidates as (
	select	cm.listCandidateId
	from	WinnerPerDistrict wpd, WICandidateMatching cm
	where	wpd.directCandidateId	= cm.directCandidateId	
),

SuccessfulDoubleCandidatesPerPartyAndState as (
	select	lc.partyId, lc.federalStateId, count(*) as num
		-- wpd is already filtered by electionId and candidateIds are unique
	from	WinnerPerDistrict wpd, WICandidateMatching cm, WIListCandidate lc
	where	wpd.directCandidateId	= cm.directCandidateId	and
		lc.id			= cm.listCandidateId
	group by lc.partyId, lc.federalStateId
)

-- add list candidates for each party and federal state
-- exclude double candidates and include following candidates (e.g. 5 and 2 double candidates -> rank <= 7)
insert 	into WIListMandateDistribution(listCandidateId, electionId)
select	lc.id, :electionId
from	SeatsPerPartyAndFederalState  spf, WIListCandidate lc, SuccessfulDoubleCandidatesPerPartyAndState dcps
where	spf.partyId		= lc.partyId							and
	spf.federalStateId	= lc.federalStateId						and
	lc.partyId		= dcps.partyId							and
	lc.federalSateId	= dcps.federalStateId						and
	lc.id			not in	(	select * from SuccessfulDoubleCandidates )	and
	lc.rank			<= (spf.seats + dcps.num)
;

insert	into WIDirectMandateDistribution(directCandidateId, electionId)
select  wpd.directCandidateId, :electionId
from	WinnerPerDistrict wpd
;