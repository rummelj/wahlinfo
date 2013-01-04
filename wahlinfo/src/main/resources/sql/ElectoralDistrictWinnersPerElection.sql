with StateWinners as (
	select	pv.federalStateId, max(pv.receivedVotes) as maxVotes
	from	WIPartyVotes pv, WIParty p
	where	pv.partyId	= p.id	and
		p.electionYear	= ':electionYear'
	group by pv.federalStateId
)

select	ed.name as edName, p.name as directVoteWinner, p1.name as listVoteWinner, fs.name
from	DirectMandatesView2009 dmv, WIDirectCandidate dc, WIParty p, WIElectoralDistrict ed, WIPartyVotes pv, StateWinners sw, WIParty p1, WIFederalState fs
where	dmv.directCandidateId	= dc.id			and
	dc.partyId		= p.id			and
	dc.electoralDistrictId	= ed.number		and
	ed.federalStateId	= pv.federalStateId	and
	pv.federalStateId	= sw.federalStateId	and
	pv.receivedVotes	= sw.maxVotes		and
	pv.partyId		= p1.id			and
	ed.federalStateId	= fs.federalStateId