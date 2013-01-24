-- which candidates can be voted in 2009 and the respective district
with EligibleCandidates as (
	select	dc.name as cName, p.name as pName
	from	WIDirectCandidate dc left outer join WIParty p on dc.partyId = p.id
	where	dc.electionYear         = '2009'	and		
		dc.electoralDistrictId	= :electoralDistrictId
),

-- votes from last year
LastYearVotes as (
	select	p.name as pName, case when pv.receivedVotes is null then 0 else pv.receivedVotes end
	from	WIParty p left outer join WIPartyVotes pv on p.id = pv.PartyId, WIElectoralDistrict ed
	where	p.electionYear		= '2005'		and
		pv.federalStateId	= ed.federalStateId	and		
		ed.number		= :electoralDistrictId
)

select	ec.cName, ec.pName
from	EligibleCandidates ec left outer join LastYearVotes lyv on ec.pName = lyv.pName		
order by lyv.receivedVotes desc, ec.cName asc