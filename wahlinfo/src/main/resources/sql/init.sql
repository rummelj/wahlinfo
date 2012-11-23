DROP TABLE IF EXISTS hibernate_sequence CASCADE;
DROP TABLE IF EXISTS WIFilledVotingPaper CASCADE;
DROP TABLE IF EXISTS WIPartyVotes CASCADE;
DROP TABLE IF EXISTS WIDirectVotes CASCADE;
DROP TABLE IF EXISTS WIListCandidate CASCADE;
DROP TABLE IF EXISTS WIDirectCandidate CASCADE;
DROP TABLE IF EXISTS WIParty CASCADE;
DROP TABLE IF EXISTS WIFederalState CASCADE;
DROP TABLE IF EXISTS WIElectoralDistrict CASCADE;
DROP TABLE IF EXISTS WIElection CASCADE;
DROP TABLE IF EXISTS WIDivisor CASCADE;
DROP TABLE IF EXISTS WIPartySeatDistribution CASCADE;
DROP TABLE IF EXISTS WIDirectMandateDistribution  CASCADE;
DROP TABLE IF EXISTS WIListMandateDistribution  CASCADE;

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
	electionId BIGINT  NOT NULL,
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
);

CREATE TABLE WIPartyVotes (
	federalStateId BIGINT  NOT NULL,
	partyId BIGINT  NOT NULL,	
	receivedVotes BIGINT  NOT NULL DEFAULT 0,
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (partyId) REFERENCES WIParty (id),	
	PRIMARY KEY (federalStateId, partyId)
);

CREATE TABLE WIDirectCandidate (
	id BIGINT  NOT NULL,
	name VARCHAR(255),
	partyId BIGINT  NOT NULL,
	electoralDistrictId SMALLINT  NOT NULL,
	electionId BIGINT  NOT NULL,
	receivedVotes BIGINT NOT NULL DEFAULT 0,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number), 
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (id)
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
	partyId BIGINT NOT NULL,
	directCandidateId BIGINT NOT NULL,	
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number),	
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),	
	PRIMARY KEY (id)
);

CREATE TABLE WIDivisor (
	id BIGINT,
	value SMALLINT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE WIPartySeatDistribution (
	partyId BIGINT,
	seats SMALLINT NOT NULL,
	electionId BIGINT,
	FOREIGN KEY (partyId) REFERENCES WIParty(id),
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	PRIMARY KEY (partyId,electionId)
);

CREATE TABLE WIDirectMandateDistribution (
	directCandidateId BIGINT,
	electionId BIGINT,
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id),
	PRIMARY KEY (directCandidateId, electionId)	
);

CREATE TABLE WIListMandateDistribution (
	listCandidateId BIGINT,
	electionId BIGINT,
	FOREIGN KEY (electionId) REFERENCES WIElection (id),
	FOREIGN KEY (listCandidateId) REFERENCES WIListCandidate (id),
	PRIMARY KEY (listCandidateId, electionId)
);
