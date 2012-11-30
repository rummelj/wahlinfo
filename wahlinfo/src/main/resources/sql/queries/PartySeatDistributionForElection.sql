VIEWS
-- parties with less than 3 votes and less than 5% of all (list) votes
BarrierClauseParties as (
	with InsuffDirectMandateParties as (
		select	wpd.partyId
		from 	WinnerPerDistrict wpd			
		group by wpd.partyId
		having	count(*) < 3
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
								p.electionId	= :electionId
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

-- the algorithm produces as many rank entries per iteration as there are qualified parties
-- therefore ceil (numSeats / numQParties) iterations (I) are required
-- in SQL these iterations are realized by calculating the cross product with the first I rows from the WIDivisor table
-- after that take and group first numSeats entries in order to get the seat distribution
Ranking as (
	select  pv.partyId, (pv.receivedVotes::float / d.value) as rvalue
	from 	WIPartyVotes pv, WIParty p, WIDivisor d
	where	pv.partyId	= p.id		and
		p.electionId	= :electionId	and
		d.id	<= ceil (598::float / ( select 	count(*) 
						from 	WIPartyVotes pv, WIParty p
						where 	pv.partyId	= p.id		and
							p.electionId	= :electionId	and
							pv.partyId	not in ( select * from BarrierClauseParties)
						)
				)
	order by rvalue desc
	limit	(select * from AvailableSeats)
)

insert into WIPartySeatDistribution(partyId, electionId, seats)
select	r.partyId, :electionId, count(*)
from	Ranking r
group by r.partyId
order by r.partyId
;