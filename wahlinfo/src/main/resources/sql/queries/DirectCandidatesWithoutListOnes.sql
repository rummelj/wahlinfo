with Opposite as (
	select 	dc.id, dc.electionId
	from 	WIDirectCandidate dc, WIElectoralDistrict ed, WIListCandidate lc
	where	dc.electoralDistrictId	= ed.number		and
		ed.federalStateId 	= lc.federalStateId 	and 
		dc.partyId 		= lc.partyId 		and
		dc.electionId = lc.electionId 
		)
select 	dc.id, dc.electionId 
from	WIDirectCandidate dc left join Opposite o on dc.id = o.id and dc.electionId = o.electionId 
where 	o.id is null


	
