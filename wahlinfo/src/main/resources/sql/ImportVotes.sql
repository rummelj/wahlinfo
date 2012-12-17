BEGIN;

ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_pkey;
ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_electoraldistrictid_fk;
ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_directiandidateid_fk;
ALTER TABLE WIFilledVotingPaper
DROP CONSTRAINT IF EXISTS wifilledvotingpaper_partyid_fk;

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
ADD CONSTRAINT wifilledvotingpaper_partyid_fk FOREIGN KEY (partyId) REFERENCES WIParty(id);
ALTER TABLE WIFilledVotingPaper
ADD CONSTRAINT wifilledvotingpaper_directcandidateid_fk FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidateId(id);
ALTER TABLE WIFilledVotingPaper
ADD CONSTRAINT wifilledvotingpaper_listcandidateid_fk FOREIGN KEY (listCandidateId) REFERENCES WIListCandidate(id);
ALTER TABLE WIFilledVotingPaper
ADD CONSTRAINT wifilledvotingpaper_pkey PRIMARY KEY (id);

END;