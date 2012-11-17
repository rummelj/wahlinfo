package com.tu.wahlinfo.csv.parser.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.csvreader.CsvReader;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.CsvDirectCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvElectoralDistrict;
import com.tu.wahlinfo.csv.entities.impl.CsvFederalState;
import com.tu.wahlinfo.csv.entities.impl.CsvGeneratedVote;
import com.tu.wahlinfo.csv.entities.impl.CsvListCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.csv.parser.ICsvParser;
import com.tu.wahlinfo.csv.parser.IVoteGenerator;

/**
 * 
 * @author cg
 */
public class CsvParser implements ICsvParser {

    private final static String AFFILIATION_DELIMITER = "_";
    private final static String NAME_ENTRY_DELIMITER = ",";
    private final static char CSV_ENTRY_DELIMITER = ';';
    private final static String FEDERAL_STATE_PATTERN_2009 = "9\\d\\d";
    private final static String EXCLUDE_BR_EL_ID = "999";
    // Path logic
    private final static String FILE_PATH_BASE_CSV = "src/main/resources/csv/";
    private final static String FILE_PATH_VOTES = FILE_PATH_BASE_CSV + "AllVotes.csv";
    private final static String FILE_PATH_CANDIDATES_2005 = FILE_PATH_BASE_CSV + "Candidates2005.csv";
    private final static String FILE_PATH_CANDIDATES_2009 = FILE_PATH_BASE_CSV + "Candidates2009.csv";
    private final static String FILE_PATH_EL_DISTRICTS_2009 = FILE_PATH_BASE_CSV + "ElectoralDistricts2009.csv";
    // Exception messages
    private final static String MSG_FILE_PATH_ERR = "Unable to open/read file [:file]";
    // Electoral districts
    private Collection<CsvElectoralDistrict> electoralDistricts = new HashSet<CsvElectoralDistrict>();
    // Candidates
    private Collection<CsvDirectCandidate> directCandidates2005 = new HashSet<CsvDirectCandidate>();
    private Collection<CsvDirectCandidate> directCandidates2009 = new HashSet<CsvDirectCandidate>();
    private Collection<CsvListCandidate> listCandidates2005 = new HashSet<CsvListCandidate>();
    private Collection<CsvListCandidate> listCandidates2009 = new HashSet<CsvListCandidate>();
    // Federal states
    private Map<String, CsvFederalState> federalStates = new HashMap<String, CsvFederalState>();
    //vote generator
    private IVoteGenerator voteGenerator = new VoteGenerator();

    public CsvParser() {
    }
    
    public Collection<CsvGeneratedVote> generateVotes(CsvVoteAggregation aggregation, long shadowIdStart) {
	return this.voteGenerator.generateVotes(aggregation, shadowIdStart);
    }

    public Collection<CsvVoteAggregation> parseVoteAggregations(int startElId, int endELId) throws CsvParserException {
	Collection<CsvVoteAggregation> voteAggregations = new HashSet<CsvVoteAggregation>();
	CsvVoteAggregation tmpAggregation;
	String partyName;
	CsvReader reader = buildCsvReader(FILE_PATH_VOTES);
	try {
	    reader.readHeaders();
	    while (reader.readRecord()) {
		if (reader.get(2).equals("99") || reader.get(1).equals("Bundesgebiet") || Integer.valueOf(reader.get(0)).intValue() < startElId) {
		    continue;
		}
		if (Integer.valueOf(reader.get(0)).intValue() > endELId) {
		    break;
		}
		tmpAggregation = new CsvVoteAggregation(reader.get(0));
		//replace with headercount vorher 30 einträge
		for (int k = 11; k < reader.getColumnCount(); k += 4) {
		    int index = k;
		    if (index == 15) {
			// skip entry for valid votes
			continue;
		    }
		    partyName = reader.getHeader(index).split(AFFILIATION_DELIMITER)[0];
		    tmpAggregation.addPartialVoteAggregation(partyName, reader.get(index + 1), reader.get(index), reader.get(index + 3),
			    reader.get(index + 2));
		}
		voteAggregations.add(tmpAggregation);
	    }
	    reader.close();
	    return voteAggregations;
	} catch (FileNotFoundException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_VOTES), ex);
	} catch (IOException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_VOTES), ex);
	}
    }

    public Collection<CsvFederalState> parseFederalStates() throws CsvParserException {
	parseDistrictsAndStates();
	return this.federalStates.values();
    }

    public Collection<CsvElectoralDistrict> parseElectoralDistricts() throws CsvParserException {
	parseDistrictsAndStates();
	return this.electoralDistricts;
    }
   
    private void parseDistrictsAndStates() throws CsvParserException {
	// already parsed?
	if (!this.electoralDistricts.isEmpty() && !this.federalStates.isEmpty()) {
	    return;
	} else {
	    // just a safety measure
	    this.electoralDistricts.clear();
	    this.federalStates.clear();
	}
	// Federal state entry is always after all related electoral districts
	// -> buffer internally to replace method
	Collection<CsvElectoralDistrict> bufferdDistricts = new HashSet<CsvElectoralDistrict>();
	CsvReader reader = buildCsvReader(FILE_PATH_EL_DISTRICTS_2009);

	try {
	    reader.readHeaders();
	    while (reader.readRecord()) {
		if (reader.get(1).equals(EXCLUDE_BR_EL_ID)) {
		    continue;
		}
		// federal state -> update buffered districts, add it to global
		// list and reset
		if (reader.get(1).matches(FEDERAL_STATE_PATTERN_2009)) {
		    this.federalStates.put(reader.get(0), new CsvFederalState(reader.get(0), reader.get(1)));
		    for (CsvElectoralDistrict district : bufferdDistricts) {
			district.setFederalStateId(reader.get(1));
		    }
		    this.electoralDistricts.addAll(bufferdDistricts);
		    bufferdDistricts.clear();
		} else {
		    bufferdDistricts.add(new CsvElectoralDistrict(reader.get(1), reader.get(2)));
		}
	    }
	    reader.close();
	} catch (IOException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_EL_DISTRICTS_2009), ex);
	}

    }
    
    public Collection<CsvDirectCandidate> parseDirectCandidates2005() throws CsvParserException {
	parseCandidates2005();
	return this.directCandidates2005;
    }
   
    public Collection<CsvListCandidate> parseListCandidates2005() throws CsvParserException {
	parseCandidates2005();
	return this.listCandidates2005;
    }
    
    private void parseCandidates2005() throws CsvParserException {
	// already parsed?
	if (!this.directCandidates2005.isEmpty() && !this.listCandidates2005.isEmpty()) {
	    return;
	} else {
	    // just a safety measure
	    this.directCandidates2005.clear();
	    this.listCandidates2005.clear();
	}
	parseDistrictsAndStates();
	String[] names;
	CsvReader reader = buildCsvReader(FILE_PATH_CANDIDATES_2005);
	try {
	    reader.readHeaders();
	    while (reader.readRecord()) {
		names = reader.get(0).split(NAME_ENTRY_DELIMITER);
		// direct candidate has no rank entry
		if (reader.get(10).isEmpty()) {
		    this.directCandidates2005.add(new CsvDirectCandidate(names[0], names[1], reader.get(2), reader.get(7), ElectionYear._2005, reader
			    .get(8)));
		} else {
		    long federalStateId = this.federalStates.get(reader.get(9)).getFederalStateId();
		    this.listCandidates2005.add(new CsvListCandidate(names[0], names[1], reader.get(2), reader.get(7), ElectionYear._2005,
			    federalStateId, reader.get(10)));
		}
	    }
	    reader.close();
	} catch (FileNotFoundException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
	} catch (IOException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
	}

    }
   
    public Collection<CsvDirectCandidate> parseDirectCandidates2009() throws CsvParserException {
	parseCandidates2009();
	return this.directCandidates2009;
    }
  
    public Collection<CsvListCandidate> parseListCandidates2009() throws CsvParserException {
	parseCandidates2009();
	return this.listCandidates2009;

    }

    private void parseCandidates2009() throws CsvParserException {
	// already parsed?
	if (!this.directCandidates2009.isEmpty() && !this.listCandidates2009.isEmpty()) {
	    return;
	} else {
	    // just a safety measure
	    this.directCandidates2009.clear();
	    this.listCandidates2009.clear();
	}
	parseDistrictsAndStates();
	CsvReader reader = buildCsvReader(FILE_PATH_CANDIDATES_2009);
	try {
	    reader.readHeaders();
	    while (reader.readRecord()) {
		// direct candidate has no rank entry
		if (reader.get(11).isEmpty()) {
		    this.directCandidates2009.add(new CsvDirectCandidate(reader.get(0), reader.get(1), reader.get(2), reader.get(3),
			    ElectionYear._2009, reader.get(4)));

		} else {
		    long federalStateId = this.federalStates.get(reader.get(5)).getFederalStateId();
		    this.listCandidates2009.add(new CsvListCandidate(reader.get(0), reader.get(1), reader.get(2), reader.get(3), ElectionYear._2009,
			    federalStateId, reader.get(6)));
		}
	    }
	    reader.close();
	} catch (FileNotFoundException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
	} catch (IOException ex) {
	    if (reader != null) {
		reader.close();
	    }
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
	}
    }

    private CsvReader buildCsvReader(String filePath) throws CsvParserException {
	try {
	    return new CsvReader(new FileReader(filePath), CSV_ENTRY_DELIMITER);
	} catch (FileNotFoundException fnfe) {
	    throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", filePath), fnfe);
	}
    }
}
