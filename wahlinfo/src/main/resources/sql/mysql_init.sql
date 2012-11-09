SET foreign_key_checks = 0;
DROP TABLE IF EXISTS WIElectionInvalidReason;
DROP TABLE IF EXISTS WIElection;
DROP TABLE IF EXISTS WIElection_EInvalidReas;
DROP TABLE IF EXISTS WIPartyVotes;
DROP TABLE IF EXISTS WIPartyRank;
DROP TABLE IF EXISTS WIDirectCandidate;
DROP TABLE IF EXISTS WIDirectVote;
DROP TABLE IF EXISTS WIListCandidate;
DROP TABLE IF EXISTS WIVoteInvalidReason;
DROP TABLE IF EXISTS WIVote_VoteInvalidReas;
DROP TABLE IF EXISTS WIParty;
DROP TABLE IF EXISTS WIFederalState;
DROP TABLE IF EXISTS WIElectoralDistrict;
DROP TABLE IF EXISTS WIFilledVotingPaper;
SET foreign_key_checks = 1;

CREATE TABLE WIElectionInvalidReason (
	id INTEGER UNSIGNED NOT NULL,
	title VARCHAR(255),
	description TEXT, 
	PRIMARY KEY (id)
);
	
CREATE TABLE WIElection (
	id INTEGER UNSIGNED NOT NULL, 
	start DATETIME, 
	end DATETIME, year YEAR, 
	PRIMARY KEY (id)
);

CREATE TABLE WIElection_EInvalidReas (
	electionId INTEGER UNSIGNED NOT NULL, 
	reasonId INTEGER UNSIGNED NOT NULL, 
	FOREIGN KEY (electionId) REFERENCES WIElection (id), 
	FOREIGN KEY (reasonId) REFERENCES WIElectionInvalidReason (id), 
	PRIMARY KEY (electionId, reasonId)
);

CREATE TABLE WIElectoralDistrict (
	id INTEGER UNSIGNED NOT NULL,
	name VARCHAR(255),
	number SMALLINT NOT NULL,
	possibleVotes INTEGER NOT NULL,
	validVotes INTEGER NOT NULL DEFAULT 0,
	invalidVotes INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
);

CREATE TABLE WIFederalState (
	id INTEGER UNSIGNED NOT NULL,
	name VARCHAR(255),
	possibleVotes INTEGER UNSIGNED NOT NULL,
	validVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	invalidVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
);

CREATE TABLE WIParty (
	id INTEGER UNSIGNED NOT NULL,
	fullName VARCHAR(255),
	shortName VARCHAR(16),
	orientation VARCHAR(64),
	PRIMARY KEY (id)
);

CREATE TABLE WIPartyVotes (
	federalStateId INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED NOT NULL,
	receivedVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (id),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	PRIMARY KEY (federalStateId, partyId)
);

CREATE TABLE WIPartyRank (
	federalStateId INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED NOT NULL,
	rank INTEGER UNSIGNED NOT NULL,
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (id),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	PRIMARY KEY (federalStateId, partyId)
);

CREATE TABLE WIDirectCandidate (
	id INTEGER UNSIGNED NOT NULL,
	profession VARCHAR(255),
	partyId INTEGER UNSIGNED NOT NULL,
	electoralDistrictId INTEGER UNSIGNED NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (id), 
	PRIMARY KEY (id)
);

CREATE TABLE WIDirectVote (
	electoralDistrictId INTEGER UNSIGNED NOT NULL,
	directCandidateId INTEGER UNSIGNED NOT NULL,
	receivedVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (id), 
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	PRIMARY KEY (electoralDistrictId, directCandidateId)
);

CREATE TABLE WIListCandidate (
	id INTEGER UNSIGNED NOT NULL,
	profession VARCHAR(255),
	rank INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED NOT NULL,
	federalStateId INTEGER UNSIGNED NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIFilledVotingPaper (
	id INTEGER UNSIGNED NOT NULL,
	electoralDistrictId INTEGER UNSIGNED NOT NULL,
	federalStateId INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED,
	directCandidateId INTEGER UNSIGNED,
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (id),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (id),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIVoteInvalidReason (
	id INTEGER UNSIGNED NOT NULL,
	title VARCHAR(255),
	description TEXT,
	affectsDirectVote BOOLEAN NOT NULL DEFAULT FALSE,
	affectsListVote BOOLEAN NOT NULL DEFAULT FALSE,
	PRIMARY KEY (id)
);

CREATE TABLE WIVote_VoteInvalidReas (
	votingPaperId INTEGER UNSIGNED NOT NULL, 
	reasonId INTEGER UNSIGNED NOT NULL, 
	FOREIGN KEY (votingPaperId) REFERENCES WIFilledVotingPaper (id), 
	FOREIGN KEY (reasonId) REFERENCES WIVoteInvalidReason (id), 
	PRIMARY KEY (votingPaperId, reasonId)
);
