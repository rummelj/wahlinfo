-- which party can be voted for in which federal state in 2009 and the respective district?
with EligibleParties as (
	select	distinct p.name
	from	WIParty p, WIListCandidate lc, WIElectoralDistrict ed
	where	p.electionYear 		= '2009'		and
		p.id			= lc.partyId		and
		lc.federalStateId	= ed.federalStateId	and		
		ed.number		= :electoralDistrictId
),

-- votes from last year
LastYearVotes as (
	select	p.name, case when pv.receivedVotes is null then 0 else pv.receivedVotes end
	from	WIParty p left outer join WIPartyVotes pv on p.id = pv.PartyId, WIElectoralDistrict ed
	where	p.electionYear		= '2005'		and
		pv.federalStateId	= ed.federalStateId	and		
		ed.number		= :electoralDistrictId
)

select	lyv.name as pName
from	EligibleParties ep, LastYearVotes lyv
where	ep.name	= lyv.name
order by lyv.receivedVotes desc, lyv.name asc




