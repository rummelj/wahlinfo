create or replace view MostConciseWinnersView:electionYear as (

	with RankedDirectCandidates as (

		select	dc.id, dc.electoralDistrictId, dc.partyId, dc.receivedVotes, rank() over (partition by dc.electoralDistrictId order by dc.receivedVotes desc)
		from	WIDirectCandidate dc
		where	dc.electionYear = ':electionYear'

	),

	WinnersDiff as (

		select	d1.id, d1.electoralDistrictId, d1.partyId, (d1.receivedVotes - d2.receivedVotes) as voteDiff
		from	RankedDirectCandidates d1, RankedDirectCandidates d2
		where	d1.electoralDistrictId	= d2.electoralDistrictId	and	
			d1.rank			= 1				and
			d2.rank			= 2
		order by d1.partyId, voteDiff

	),

	RankedWinnersDiff as (

		select	wd.*, rank() over (partition by wd.partyId order by voteDiff asc) as rank
		from	WinnersDiff wd

	),

	WinnerTopTen as (

		select	p.name as pName, dc.name as cName, ed.name as edName, fs.name as fsName, rwd.voteDiff, rwd.rank
		from	RankedWinnersDiff rwd, WIDirectCandidate dc, WIParty p, WIElectoralDistrict ed, WIFederalState fs
		where	rwd.rank            <= 10               and
			rwd.id              = dc.id             and
			rwd.partyId         = p.id              and
			rwd.electoralDistrictId	= ed.number     and
                        ed.federalStateId   = fs.federalStateId 
		order by p.name asc, rwd.rank asc

	),

	NonWinnerParties as (

		select	p.id
		from	WIParty p
		where	p.id not in	(	select distinct dc.partyId
						from		DirectMandatesView:electionYear dmv, WIDirectCandidate dc
						where		dmv.directCandidateId	= dc.id
					)	and
			p.electionYear = ':electionYear'

	),

	LooserCandidates as (

		select	dc.*
		from	WIDirectCandidate dc
		where	dc.partyId	in	(	select	nwp.id
							from	NonWinnerParties nwp
						)

	),

	LooserCandidatesDiff as (

		select	p.name as pName, lc.name as cName, ed.name as edName, fs.name as fsName, (lc.receivedVotes - dc.receivedVotes) as voteDiff, rank() over (partition by lc.partyId order by (lc.receivedVotes - dc.receivedVotes) desc)
		from	LooserCandidates lc, DirectMandatesView:electionYear dmv, WIParty p, WIElectoralDistrict ed, WIDirectCandidate dc, WIFederalState fs
		where	dmv.directCandidateId	= dc.id				and
			lc.electoralDistrictId	= dc.electoralDistrictId	and
			lc.partyId		= p.id				and
			lc.electoralDistrictId	= ed.number                     and
                        ed.federalStateId       = fs.federalStateId
		
	),

	LooserTopTen as (

		select	*
		from	LooserCandidatesDiff lcd
		where	lcd.rank <= 10

	)

	select	*
	from	WinnerTopTen
	union
	select 	* 
	from 	LooserTopTen
        order by pname, rank
);