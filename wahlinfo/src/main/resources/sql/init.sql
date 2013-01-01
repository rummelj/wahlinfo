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
DROP TABLE IF EXISTS WIElectoralDistrictVoteData CASCADE;
-- no longer used in further releases
DROP TABLE IF EXISTS WIPartySeatDistribution CASCADE;
DROP TABLE IF EXISTS WIStatePartySeatDistribution  CASCADE;
DROP TABLE IF EXISTS WIDirectMandateDistribution  CASCADE;
DROP TABLE IF EXISTS WIListMandateDistribution  CASCADE;

CREATE TABLE hibernate_sequence(
	next_val BIGINT NOT NULL,
	PRIMARY KEY (next_val) 
);

INSERT INTO hibernate_sequence VALUES (0);

CREATE TABLE WIElection (
	electionYear VARCHAR(4) NOT NULL, 
	PRIMARY KEY (electionYear)
);

CREATE TABLE WIFederalState (
	federalStateId BIGINT  NOT NULL,
	name VARCHAR(255),	
	PRIMARY KEY (federalStateId)
);

CREATE TABLE WIElectoralDistrict (
	number SMALLINT  NOT NULL,
	name VARCHAR(255),
	federalStateId BIGINT,	
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	PRIMARY KEY (number)
);

CREATE TABLE WIParty (
	id BIGINT  NOT NULL,
	name VARCHAR(255),
	electionYear VARCHAR(4)  NOT NULL,
	FOREIGN KEY (electionYear) REFERENCES WIElection (electionYear),
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
	partyId BIGINT,
	electoralDistrictId SMALLINT  NOT NULL,
	electionYear VARCHAR(4)  NOT NULL,
	receivedVotes BIGINT NOT NULL DEFAULT 0,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number), 
	FOREIGN KEY (electionYear) REFERENCES WIElection (electionYear),
	PRIMARY KEY (id)
);

CREATE TABLE WIListCandidate (
	id BIGINT  NOT NULL,
	name VARCHAR(255),
	profession VARCHAR(255),
	rank BIGINT  NOT NULL,
	partyId BIGINT  NOT NULL,
	federalStateId BIGINT  NOT NULL,
	electionYear VARCHAR(4)  NOT NULL,
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (federalStateId) REFERENCES WIFederalState (federalStateId),
	FOREIGN KEY (electionYear) REFERENCES WIElection (electionYear),
	PRIMARY KEY (id)
);

CREATE TABLE WIFilledVotingPaper (	
	electoralDistrictId SMALLINT NOT NULL,	
	partyId BIGINT,
	directCandidateId BIGINT,	
	FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number),	
	FOREIGN KEY (partyId) REFERENCES WIParty (id),
	FOREIGN KEY (directCandidateId) REFERENCES WIDirectCandidate (id)	
);

CREATE TABLE WIDivisor (
	id BIGINT,
	value SMALLINT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE WIElectoralDistrictVoteData (
        electoralDistrictId BIGINT,
        electionYear VARCHAR(4),
        possibleVotes INTEGER NOT NULL DEFAULT(0),
        submittedVotes INTEGER NOT NULL DEFAULT(0),
        validDirectVotes INTEGER NOT NULL DEFAULT(0),
        validPartyVotes INTEGER NOT NULL DEFAULT(0),
        invalidDirectVotes INTEGER NOT NULL DEFAULT(0),
        invalidPartyVotes INTEGER NOT NULL DEFAULT(0),
        FOREIGN KEY (electoralDistrictId) REFERENCES WIElectoralDistrict (number),
        FOREIGN KEY (electionYear) REFERENCES WIElection (electionYear),
        PRIMARY KEY (electoralDistrictId, electionYear)
);