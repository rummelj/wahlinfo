SET foreign_key_checks = 0;
DROP TABLE IF EXISTS WIElection;
DROP TABLE IF EXISTS WIPartyVotes;
DROP TABLE IF EXISTS WIPartyRank;
DROP TABLE IF EXISTS WIDirectCandidate;
DROP TABLE IF EXISTS WIDirectVote;
DROP TABLE IF EXISTS WIListCandidate;
DROP TABLE IF EXISTS WIParty;
DROP TABLE IF EXISTS WIFederalState;
DROP TABLE IF EXISTS WIElectoralDistrict;
DROP TABLE IF EXISTS WIFilledVotingPaper;
SET foreign_key_checks = 1;

CREATE TABLE WIElection (
	id INTEGER UNSIGNED NOT NULL, 
	start DATETIME, 
	end DATETIME, year YEAR NOT NULL, 
	PRIMARY KEY (id)
);

CREATE TABLE WIElectoralDistrict (
	number SMALLINT UNSIGNED NOT NULL,
	name VARCHAR(255),
	possibleVotes INTEGER NOT NULL,
	validVotes INTEGER NOT NULL DEFAULT 0,
	invalidVotes INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY (number)
);

CREATE TABLE WIFederalState (
	federalStateId INTEGER UNSIGNED NOT NULL,
	name VARCHAR(255),
	possibleVotes INTEGER UNSIGNED NOT NULL,
	validVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	invalidVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	PRIMARY KEY (federalStateId)
);

CREATE TABLE WIParty (
	id INTEGER UNSIGNED NOT NULL,
	name VARCHAR(255),
	PRIMARY KEY (id)
);

CREATE TABLE WIPartyVotes (
	federalStateId INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED NOT NULL,
	electionId INTEGER UNSIGNED NOT NULL,
	receivedVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (federalStateId, partyId)
);

CREATE TABLE WIDirectCandidate (
	id INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED NOT NULL,
	electoralDistrictId SMALLINT UNSIGNED NOT NULL,
	electionId INTEGER UNSIGNED NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number), 
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIDirectVote (
	electoralDistrictId SMALLINT UNSIGNED NOT NULL,
	directCandidateId INTEGER UNSIGNED NOT NULL,
	receivedVotes INTEGER UNSIGNED NOT NULL DEFAULT 0,
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number), 
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	PRIMARY KEY (electoralDistrictId, directCandidateId)
);

CREATE TABLE WIListCandidate (
	id INTEGER UNSIGNED NOT NULL,
	profession VARCHAR(255),
	rank INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED NOT NULL,
	federalStateId INTEGER UNSIGNED NOT NULL,
	electionId INTEGER UNSIGNED NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIFilledVotingPaper (
	id INTEGER UNSIGNED NOT NULL,
	electoralDistrictId SMALLINT UNSIGNED NOT NULL,
	federalStateId INTEGER UNSIGNED NOT NULL,
	partyId INTEGER UNSIGNED,
	directCandidateId INTEGER UNSIGNED,
	electionId INTEGER UNSIGNED NOT NULL,
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);
