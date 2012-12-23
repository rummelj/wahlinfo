BEGIN;

ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_electoraldistrictid_fkey;
ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_directcandidateid_fkey;
ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_partyid_fkey;

COPY	WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid) 
FROM	':filePath0'
WITH 	(FORMAT CSV, DELIMITER ';');

COPY	WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid)
FROM	':filePath1'
WITH 	(FORMAT CSV, DELIMITER ';');

COPY	WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid)
FROM	':filePath2'
WITH 	(FORMAT CSV, DELIMITER ';');

COPY	WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid)
FROM	':filePath3'
WITH 	(FORMAT CSV, DELIMITER ';');

COPY	WIFilledVotingPaper (electoraldistrictid, directcandidateid, partyid)
FROM	':filePath4'
WITH 	(FORMAT CSV, DELIMITER ';');

ALTER TABLE WIFilledVotingPaper
ADD CONSTRAINT wifilledvotingpaper_electoraldistrictid_fkey FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict(number);
ALTER TABLE WIFilledVotingPaper
ADD CONSTRAINT wifilledvotingpaper_directcandidateid_fkey FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate(id);
ALTER TABLE WIFilledVotingPaper
ADD CONSTRAINT wifilledvotingpaper_partyid_fkey FOREIGN KEY (partyId) REFERENCES WIParty(id);

END;