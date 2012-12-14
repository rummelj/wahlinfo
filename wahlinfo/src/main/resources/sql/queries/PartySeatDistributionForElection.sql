VIEWS
,
-- parties with less than 3 votes and less than 5% of all (list) votes
BarrierClauseParties as (
	with InsuffDirectMandateParties as (
		select	p.id
		from	WIParty p
		where	p.electionYear 	= :electionYear					and
			p.id		not in	(	select	wpd.partyId
							from	WinnerPerDistrict wpd			
							group by wpd.partyId
							having	count(*) < 3	
						)
	)		
	-- WinnerPerDistrict is already filtered by election and PartyIds are unique
	select	pv.partyId
	from 	WIPartyVotes pv
	where	pv.partyId	in	(	select *
						from InsuffDirectMandateParties
					)											
	group by pv.partyId
	having	sum(pv.receivedVotes)	<	(	select 	0.05 * sum(receivedVotes)
							from	WIPartyVotes pv1, WIParty p
							where	pv1.partyId	= p.id	and
								p.electionYear	= :electionYear
						)
),

-- 598 - (Winners without party) - (Winners of barrier clause party)
AvailableSeats as (
	select 	598 - count(*)
		-- filtered for election
	from	WinnerPerDistrict wpd
	where	wpd.partyId		in	(	select 	*
								-- filtered for election
							from 	BarrierClauseParties
						)	or		
		wpd.partyId		is null
		
),

-- all qualified parties and the sum of votes they received
QualifiedPartiesAndVotes as (
	select	pv.partyId, sum(pv.receivedVotes) as receivedVotes
	from	WIPartyVotes pv, WIParty p
	where	p.electionYear 	= :electionYear	and
		p.id		= pv.partyId	and
		p.id		not in	(	select	*
						from	BarrierClauseParties
					)
	group by pv.partyId
),

-- the algorithm produces as many rank entries per iteration as there are qualified parties
-- however as a single party could have all top rank values, (available seats) many iterations (I) are required
-- in SQL these iterations are realized by calculating the cross product with the first I rows from the WIDivisor table
-- after that group first the (numSeats) many entries in order to get the seat distribution
Ranking as (
	select  qpv.partyId, (qpv.receivedVotes::float / d.value) as rvalue
	from 	QualifiedPartiesAndVotes qpv, WIDivisor d
	where 	d.id <= (select * from AvailableSeats)
	order by rvalue desc
	limit	(select * from AvailableSeats)
)

insert into WIPartySeatDistribution(partyId, electionYear, seats)
select	r.partyId, :electionYear, count(*)
from	Ranking r
group by r.partyId
order by r.partyId
;