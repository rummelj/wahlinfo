-- which candidates can be voted in 2009 and the respective district
with EligibleCandidates as (
	select	dc.name as cName, p.name as pName
	from	WIDirectCandidate dc left outer join WIParty p on dc.partyId = p.id
	where	dc.electionYear         = '2009'	and		
		dc.electoralDistrictId	= :electoralDistrictId and
                not (p.name             = 'Uebrige') 
),

-- votes from last year
LastYearVotes as (
	select	p.name as pName, pv.receivedVotes
	from	WIParty p left outer join WIPartyVotes pv on p.id = pv.PartyId, WIElectoralDistrict ed
	where	p.electionYear		= '2005'		and
		pv.federalStateId	= ed.federalStateId	and		
		ed.number		= :electoralDistrictId
)

select	ec.cName as cName, ec.pName as pName
from	EligibleCandidates ec left outer join LastYearVotes lyv on ec.pName = lyv.pName		
order by lyv.receivedVotes desc nulls last, ec.cName asc;
