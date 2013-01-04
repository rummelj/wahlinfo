with AllVotes as (

	select	p.electionYear, count(*) as votes
	from	WIFilledVotingPaper fvp, WIParty p
	where	fvp.electoralDistrictId	= :number	and
		fvp.partyId		= p.id		
	group by p.electionYear
),

VotesPerParty as (

	select	p.name, p.electionYear, count(*) as receivedVotes
	from	WIFilledVotingPaper fvp, WIParty p
	where	fvp.electoralDistrictId	= :number	and
		fvp.partyId		= p.id
	group by p.name, p.electionYear
)

select	vp1.name as pName, vp1.receivedVotes, (vp1.receivedVotes / (cast (av1.votes as float))) as percentVotes, (vp1.receivedVotes - vp2.receivedVotes) as voteDiff, ((vp1.receivedVotes / (cast (av1.votes as float))) - (vp2.receivedVotes / (cast (av2.votes as float)))) as percentDiff
from	AllVotes av1, VotesPerParty vp1, VotesPerParty vp2, AllVotes av2
where	av1.electionYear	= vp1.electionYear	and
	vp1.electionYear	= '2009'		and
	vp1.name		= vp2.name		and
	vp2.electionYear	= '2005'		and
	vp2.electionYear	= av2.electionYear
order by vp1.name