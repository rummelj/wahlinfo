DROP TABLE IF EXISTS hibernate_sequence;
DROP TABLE IF EXISTS WIFilledVotingPaper;
DROP TABLE IF EXISTS WIPartyVotes;
DROP TABLE IF EXISTS WIDirectVotes;
DROP TABLE IF EXISTS WIListCandidate;
DROP TABLE IF EXISTS WIDirectCandidate;
DROP TABLE IF EXISTS WIParty;
DROP TABLE IF EXISTS WIFederalState;
DROP TABLE IF EXISTS WIElectoralDistrict;
DROP TABLE IF EXISTS WIElection;

CREATE TABLE hibernate_sequence(
	next_val BIGINT NOT NULL,
	PRIMARY KEY (next_val) 
);

INSERT INTO hibernate_sequence VALUES (0);

CREATE TABLE WIElection (
	id BIGINT  NOT NULL, 
	start int, 
	ending int NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE WIFederalState (
	federalStateId BIGINT  NOT NULL,
	name VARCHAR(255),
	possibleVotes BIGINT  NOT NULL,
	validVotes BIGINT  NOT NULL DEFAULT 0,
	invalidVotes BIGINT  NOT NULL DEFAULT 0,
	PRIMARY KEY (federalStateId)
);

CREATE TABLE WIElectoralDistrict (
	number SMALLINT  NOT NULL,
	name VARCHAR(255),
	federalStateId BIGINT,
	possibleVotes BIGINT NOT NULL,
	validVotes BIGINT NOT NULL DEFAULT 0,
	invalidVotes BIGINT NOT NULL DEFAULT 0,
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	PRIMARY KEY (number)
);

CREATE TABLE WIParty (
	id BIGINT  NOT NULL,
	name VARCHAR(255),
	PRIMARY KEY (id)
);

CREATE TABLE WIPartyVotes (
	federalStateId BIGINT  NOT NULL,
	partyId BIGINT  NOT NULL,
	electionId BIGINT  NOT NULL,
	receivedVotes BIGINT  NOT NULL DEFAULT 0,
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (federalStateId, partyId)
);

CREATE TABLE WIDirectCandidate (
	id BIGINT  NOT NULL,
	name VARCHAR(255),
	partyId BIGINT  NOT NULL,
	electoralDistrictId SMALLINT  NOT NULL,
	electionId BIGINT  NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number), 
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIDirectVotes (
	electoralDistrictId SMALLINT  NOT NULL,
	directCandidateId BIGINT  NOT NULL,
	electionId BIGINT  NOT NULL,
	receivedVotes BIGINT  NOT NULL DEFAULT 0,
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number), 
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (electoralDistrictId, directCandidateId)
);

CREATE TABLE WIListCandidate (
	id BIGINT  NOT NULL,
	name VARCHAR(255),
	profession VARCHAR(255),
	rank BIGINT  NOT NULL,
	partyId BIGINT  NOT NULL,
	federalStateId BIGINT  NOT NULL,
	electionId BIGINT  NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIFilledVotingPaper (
	id BIGINT  NOT NULL,
	electoralDistrictId SMALLINT  NOT NULL,
	federalStateId BIGINT  NOT NULL,
	partyId BIGINT ,
	directCandidateId BIGINT ,
	electionId BIGINT  NOT NULL,
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);
