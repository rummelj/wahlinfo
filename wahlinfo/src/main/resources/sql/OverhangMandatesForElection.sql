with MandatesPerPartyAndState as (
    
    select  ed.federalStateId, dc.partyId, count(*) as num
    from    DirectMandatesView:electionYear dmv, WIDirectCandidate dc, WIElectoralDistrict ed
    where   dmv.directCandidateId   = dc.id     and
            dc.electoralDistrictId  = ed.number            
    group by ed.federalStateId, dc.partyId
)

select  fs.name as fsName, p.name as pName, (mps.num - ldv.seats) as overhangMandates
from    MandatesPerPartyAndState mps, LowerDistributionView:electionYear ldv, WIFederalState fs, WIParty p
where   mps.federalStateId  = ldv.federalStateId    and
        ldv.federalStateId  = fs.federalStateId     and
        mps.partyId         = ldv.partyId           and
        ldv.partyId         = p.id                  and
        mps.num             > ldv.seats
;