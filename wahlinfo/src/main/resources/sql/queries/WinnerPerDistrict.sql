with MaxVotes as (
	select dv.electoralDistrictId, dv.electionId, max(dv.receivedVotes) as votes
	from WIDirectVotes dv
	group by dv.electoralDistrictId, dv.electionId
)

select 	distinct on (dv.electoralDistrictId) dv.electoralDistrictId, dv.directCandidateId, dv.electionId
from 	MaxVotes m, WIDirectVotes dv
where	m.electoralDistrictId 	= dv.electoralDistrictId 	and
	m.electionId		= dv.electionId			and
	m.votes			= dv.receivedVotes
order by dv.electoralDistrictId, random()


	
