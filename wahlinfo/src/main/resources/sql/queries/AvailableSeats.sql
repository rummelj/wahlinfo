REQUIRES: DirectCandidatesWithoutListOnes, PartiesBelowFivePercent, WinnerPerDistrict

select	598 - count(dc.id) as AvailableSeats, dc.electionId
from	WinnerPerDistrict wpd, WIDirectCandidate dc
where 	wpd.directCandidateId 	= dc.id 	and
	wpd.electionId 		= dc.electionId	and
	(	dc.partyId	is null	or
		dc.id 		in (	select	d.id 
					from 	DirectCandidatesWithoutListOnes d
					where 	d.electionId	= dc.electionId
				)	or
		dc.partyId 	in (	select	p.partyId
					from	PartiesBelowFivePercent p
					where	p.electionId = dc.electionId
				)
	)

group by dc.electionId