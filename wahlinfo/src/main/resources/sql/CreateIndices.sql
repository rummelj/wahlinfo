CREATE INDEX wifilledvotingpaper_directcandidateid_index ON WIFilledVotingPaper USING BTREE (directCandidateId);
CREATE INDEX wifilledvotingpaper_partyid_index ON WIFilledVotingPaper USING BTREE (partyId);
CREATE INDEX wifilledvotingpaper_electoraldistrictid_index ON WIFilledVotingPaper USING BTREE (electoralDistrictId);