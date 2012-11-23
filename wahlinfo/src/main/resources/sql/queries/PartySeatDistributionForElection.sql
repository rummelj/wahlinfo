delete 
from	WIPartySeatDistribution psd
where	psd.electionId	= :electionId;

delete
from 	WIDirectMandateDistribution dmd
where	dmd.electionId	= :electionId;

delete
from 	WIListMandateDistribution lmd
where	lmd.electionId	= :electionId;

drop view if exists WinnerPerDistrict;

create view WinnerPerDistrict as (
	with MaxVotes as (
		select	dc.electoralDistrictId, max(dc.receivedVotes) as receivedVotes
		from	WIDirectCandidate dc
		where	dc.electionID	= :electionId
		group by dc.electoralDistrictId
	)
	select	distinct on (dc.electoralDistrictId) dc.electoralDistrictId, dc.id as directCandidateId, dc.partyId
	from	MaxVotes m, WIDirectCandidate dc
	where	m.electoralDistrictId	= dc.electoralDistrictId	and
		m.receivedVotes		= dc.receivedVotes		and
		dc.electionId		= :electionId
	order by dc.electoralDistrictId, random()
);

with DirectVoteWinnerWithoutPartyOrIndependent as (
	select	wpd.directCandidateId
	from	WinnerPerDistrict wpd
	where	wpd.partyId is null	or
		not exists 	(	select	*
					from	WIListCandidate lc, WIElectoralDistrict ed
					where	wpd.partyId		= lc.partyId		and
						wpd.electoralDistrictId	= ed.number		and
						ed.federalStateId	= lc.federalStateID	and
						lc.electionId		= :electionId
				)		
),

BarrierClauseParties as (
	with InsuffDirectMandateParties as (
		select	wpd.partyId
		from 	WinnerPerDistrict wpd			
		group by wpd.partyId
		having	count(*) < 3
	)	
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

AvailableSeats as (
	select 	598 - count(*)
	from	WinnerPerDistrict wpd
	where	wpd.partyId		not in	(	select 	*
							from BarrierClauseParties
						)	and
		wpd.directCandidateId	not in	(	select	*
							from	DirectVoteWinnerWithoutPartyOrIndependent
						)
),

QualifiedVotesPerParty as (
	select	fvp.partyId, count(fvp.id) as votes
	from	WIFilledVotingPaper fvp, WIParty p
	where	fvp.partyId	= p.id	and
		p.electionId	= :electionId	and
		fvp.partyId		not in	(	select	*
							from	BarrierClauseParties
						)
					and
		fvp.directCandidateId	not in	(	select	*
							from	DirectVoteWinnerWithoutPartyOrIndependent
						)
	group by fvp.partyId

),

Ranking as (
	select  qvp.partyId, (qvp.votes::float / d.value) as rvalue
	from 	QualifiedVotesPerParty qvp, WIDivisor d
	where	d.id	<= ceil (598::float / ( select count(*) from QualifiedVotesPerParty ))
	order by rvalue desc
	limit	(select * from AvailableSeats)
)

insert into WIPartySeatDistribution(partyId, electionId, seats)
select	r.partyId, :electionId, count(*)
from	Ranking r
group by r.partyId
order by r.partyId
;