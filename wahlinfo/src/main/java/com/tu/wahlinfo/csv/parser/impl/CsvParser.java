package com.tu.wahlinfo.csv.parser.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.CsvDirectCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvElection;
import com.tu.wahlinfo.csv.entities.impl.CsvElectoralDistrict;
import com.tu.wahlinfo.csv.entities.impl.CsvFederalState;
import com.tu.wahlinfo.csv.entities.impl.CsvListCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvParty;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.csv.parser.ICsvParser;

/**
 * 
 * @author cg
 */
public class CsvParser implements ICsvParser {

	// used for fixed party names
	private final static long PARTY_ID_START_NUM = 1;
	private final static String AFFILIATION_DELIMITER = "_";
	private final static String NAME_ENTRY_DELIMITER = ",";
	private final static char CSV_ENTRY_DELIMITER = ';';
	private final static String FEDERAL_STATE_PATTERN_2009 = "9\\d\\d";
	private final static String EXCLUDE_BR_EL_ID = "999";
	// Path logic
	private final static String FILE_PATH_BASE_CSV = "/csv/";
	private final static String FILE_PATH_VOTES = FILE_PATH_BASE_CSV
			+ "AllVotes.csv";
	private final static String FILE_PATH_CANDIDATES_2005 = FILE_PATH_BASE_CSV
			+ "Candidates2005.csv";
	private final static String FILE_PATH_CANDIDATES_2009 = FILE_PATH_BASE_CSV
			+ "Candidates2009.csv";
	private final static String FILE_PATH_EL_DISTRICTS_2009 = FILE_PATH_BASE_CSV
			+ "ElectoralDistricts2009.csv";
	// Exception messages
	private final static String MSG_FILE_PATH_ERR = "Unable to open/read file [:file]";
	// Electoral districts
	private Collection<CsvElectoralDistrict> electoralDistricts = new HashSet<CsvElectoralDistrict>();
	// Candidates
	private Collection<CsvDirectCandidate> directCandidates2005 = new HashSet<CsvDirectCandidate>();
	private Collection<CsvDirectCandidate> directCandidates2009 = new HashSet<CsvDirectCandidate>();
	private Collection<CsvListCandidate> listCandidates2005 = new HashSet<CsvListCandidate>();
	private Collection<CsvListCandidate> listCandidates2009 = new HashSet<CsvListCandidate>();
	// Sorted direct candidates: elDir -> party -> candidateId
	private Map<Long, HashMap<Long, Long>> sortedDirectCandidates2005 = new HashMap<Long, HashMap<Long, Long>>();
	private Map<Long, HashMap<Long, Long>> sortedDirectCandidates2009 = new HashMap<Long, HashMap<Long, Long>>();
	// Federal states
	private Map<String, CsvFederalState> federalStates = new HashMap<String, CsvFederalState>();
	// Party matching
	private Map<ElectionYear, HashMap<String, Long>> partyIds = new EnumMap<ElectionYear, HashMap<String, Long>>(
			ElectionYear.class);
	private Map<String, String> partyNameReplacements = new HashMap<String, String>();

	public CsvParser() {
		this.initPartyNameReplacements();
		this.partyIds.put(ElectionYear._2005, new HashMap<String, Long>());
		this.partyIds.put(ElectionYear._2009, new HashMap<String, Long>());
	}

	/**
	 * Creates a definite number of name replacements for parties having the
	 * value to be replaced as key and its replacement as value. This is to
	 * prevent the creation of false duplicates (e.g. "DIE LINKE" &
	 * "Die Linke."). All entries do not conform to the specification of
	 * votes-csv-file which is why they are excluded.
	 * 
	 */
	private void initPartyNameReplacements() {
		this.partyNameReplacements.put("Die Linke.", "DIE LINKE");
		this.partyNameReplacements.put("Tierschutz", "Die Tierschutzpartei");
		this.partyNameReplacements.put("VIOLETTEN", "DIE VIOLETTEN");
		this.partyNameReplacements.put("Volksabst.", "Volksabstimmung");
	}

	private Long getPartyId(ElectionYear year, String partyName) {
		if (partyName.equals("")) {
			return null;
		} else {
			return this.partyIds.get(year).get(partyName);
		}
	}

	/**
	 * Processes a potentially new party name. First checks if a replacement
	 * exists. If so, it is used for the second check. During that the resulting
	 * party name is compared with the ones already seen.
	 * 
	 * @param pname
	 * @return Null if the second check is positive. Otherwise the argument or
	 *         its replacement in case it exists.
	 */
	private String processNewPartyName(ElectionYear year, String pname) {
		if (pname.isEmpty()) {
			return null;
		}
		String replacement = this.partyNameReplacements.get(pname);
		replacement = (replacement == null) ? pname : replacement;
		if (this.partyIds.get(year).containsKey(replacement)) {
			return null;
		} else {
			return replacement;
		}
	}

	@Override
	public Collection<CsvParty> parseParties() throws CsvParserException {
		Collection<CsvParty> parties = processParseParties(
				FILE_PATH_CANDIDATES_2005, ElectionYear._2005, 7);
		parties.addAll(processParseParties(FILE_PATH_CANDIDATES_2009,
				ElectionYear._2009, 3));
		// manually add "non-real" parties which don't occur in candidates csv
		parties.add(this.processNewCsvParty(0L, "Uebrige", ElectionYear._2005));
		parties.add(this.processNewCsvParty(0L, "Uebrige", ElectionYear._2009));
		return parties;
	}

	private CsvParty processNewCsvParty(long nonPrefixedPositiveId,
			String partyName, ElectionYear year) {
		long prefixedId = Long.parseLong(year.getPrefix() + ""
				+ nonPrefixedPositiveId);
		this.partyIds.get(year).put(partyName, prefixedId);
		return new CsvParty(prefixedId, partyName, year);
	}

	private Collection<CsvParty> processParseParties(String filePath,
			ElectionYear year, int partyNameIndex) throws CsvParserException {
		Collection<CsvParty> parties = new HashSet<CsvParty>();
		CsvReader reader = buildCsvReader(filePath);
		long index = PARTY_ID_START_NUM;
		String partyName;
		try {
			reader.readHeaders();
			while (reader.readRecord()) {
				partyName = this.processNewPartyName(year,
						reader.get(partyNameIndex));
				if (partyName != null) {
					parties.add(this.processNewCsvParty(index++, partyName,
							year));
				}
			}
			reader.close();
			return parties;
		} catch (IOException ex) {
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					filePath));
		}
	}

	public Collection<CsvVoteAggregation> parseVoteAggregations(int startElId,
			int endELId) throws CsvParserException {
		Collection<CsvVoteAggregation> voteAggregations = new HashSet<CsvVoteAggregation>();
		CsvVoteAggregation tmpAggregation;
		String partyName;
		Long partyId2005 = null;
		Long partyId2009 = null;
		Long directCandidateId2005 = null;
		Long directCandidateId2009 = null;
		if (this.partyIds.get(ElectionYear._2005).isEmpty()) {
			parseParties();
		}
		createSortedCandidates();
		CsvReader reader = buildCsvReader(FILE_PATH_VOTES);
		try {
			reader.readHeaders();
			while (reader.readRecord()) {
				if (reader.get(2).equals("99")
						|| reader.get(1).equals("Bundesgebiet")
						|| Integer.valueOf(reader.get(0)).intValue() < startElId) {
					continue;
				}
				if (Integer.valueOf(reader.get(0)).intValue() > endELId) {
					break;
				}
				tmpAggregation = new CsvVoteAggregation(reader.get(0));
				for (int k = 11; k < reader.getHeaderCount(); k += 4) {
					int index = k;
					if (index == 15) {
						// skip entry for valid votes
						continue;
					}
					partyName = reader.getHeader(index).split(
							AFFILIATION_DELIMITER)[0];
					partyId2005 = this
							.getPartyId(ElectionYear._2005, partyName);
					partyId2009 = this
							.getPartyId(ElectionYear._2009, partyName);
					// first iteration is invalid votes -> null values
					if (index != 11) {
						directCandidateId2005 = this.sortedDirectCandidates2005
								.get(Long.parseLong(reader.get(0))).get(
										partyId2005);
						directCandidateId2009 = this.sortedDirectCandidates2009
								.get(Long.parseLong(reader.get(0))).get(
										partyId2009);
					}

					tmpAggregation.addPartialVoteAggregation(
							directCandidateId2005, directCandidateId2009,
							partyId2005, partyId2009, reader.get(index + 1),
							reader.get(index), reader.get(index + 3),
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
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_VOTES), ex);
		} catch (IOException ex) {
			if (reader != null) {
				reader.close();
			}
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_VOTES), ex);
		}
	}
	/**
	 * Stores candidates in a two level map as: electoralDistrictId -> partyId
	 * -> CsvDirectCandidate
	 * 
	 * @throws CsvParserException
	 */
	private void createSortedCandidates() throws CsvParserException {
		if (!this.sortedDirectCandidates2005.isEmpty()) {
			return;
		}
		if (this.directCandidates2005.isEmpty()) {
			parseCandidates2005();
		}
		for (CsvDirectCandidate dirCan : this.directCandidates2005) {
			if (this.sortedDirectCandidates2005.get(dirCan
					.getElectoralDistrictId()) == null) {
				this.sortedDirectCandidates2005.put(
						dirCan.getElectoralDistrictId(),
						new HashMap<Long, Long>());
			}
			this.sortedDirectCandidates2005
					.get(dirCan.getElectoralDistrictId()).put(
							dirCan.getPartyId(), dirCan.getId());
		}
		if (!this.sortedDirectCandidates2009.isEmpty()) {
			return;
		}
		if (this.directCandidates2009.isEmpty()) {
			parseCandidates2009();
		}
		for (CsvDirectCandidate dirCan : this.directCandidates2009) {
			if (this.sortedDirectCandidates2009.get(dirCan
					.getElectoralDistrictId()) == null) {
				this.sortedDirectCandidates2009.put(
						dirCan.getElectoralDistrictId(),
						new HashMap<Long, Long>());
			}
			this.sortedDirectCandidates2009
					.get(dirCan.getElectoralDistrictId()).put(
							dirCan.getPartyId(), dirCan.getId());
		}
	}

	public Collection<CsvFederalState> parseFederalStates()
			throws CsvParserException {
		parseDistrictsAndStates();
		return this.federalStates.values();
	}

	public Collection<CsvElectoralDistrict> parseElectoralDistricts()
			throws CsvParserException {
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
					this.federalStates.put(reader.get(0), new CsvFederalState(
							reader.get(0), reader.get(1)));
					for (CsvElectoralDistrict district : bufferdDistricts) {
						district.setFederalStateId(reader.get(1));
					}
					this.electoralDistricts.addAll(bufferdDistricts);
					bufferdDistricts.clear();
				} else {
					bufferdDistricts.add(new CsvElectoralDistrict(
							reader.get(1), reader.get(2)));
				}
			}
			reader.close();
		} catch (IOException ex) {
			if (reader != null) {
				reader.close();
			}
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_EL_DISTRICTS_2009), ex);
		}

	}

	public Collection<CsvDirectCandidate> parseDirectCandidates2005()
			throws CsvParserException {
		parseCandidates2005();
		return this.directCandidates2005;
	}

	public Collection<CsvListCandidate> parseListCandidates2005()
			throws CsvParserException {
		parseCandidates2005();
		return this.listCandidates2005;
	}

	private void parseCandidates2005() throws CsvParserException {
		// already parsed?
		if (!this.directCandidates2005.isEmpty()
				&& !this.listCandidates2005.isEmpty()) {
			return;
		} else {
			// just a safety measure
			this.directCandidates2005.clear();
			this.listCandidates2005.clear();
		}
		parseDistrictsAndStates();
		if (this.partyIds.get(ElectionYear._2005).isEmpty()) {
			parseParties();
		}
		String partyName;
		String[] names;
		long id = 1;
		long prefixedId;
		CsvReader reader = buildCsvReader(FILE_PATH_CANDIDATES_2005);
		try {
			reader.readHeaders();
			while (reader.readRecord()) {
				names = reader.get(0).split(NAME_ENTRY_DELIMITER);
				partyName = (this.partyNameReplacements.get(reader.get(7)) == null)
						? reader.get(7)
						: this.partyNameReplacements.get(reader.get(7));
				prefixedId = Long.parseLong(ElectionYear._2005.getPrefix() + ""
						+ id++);
				// direct candidate has an electoral district id
				if (!reader.get(8).isEmpty()) {
					this.directCandidates2005.add(new CsvDirectCandidate(
							prefixedId, names[0], names[1], reader.get(2), this
									.getPartyId(ElectionYear._2005, partyName),
							ElectionYear._2005, reader.get(8)));
					// list candidate has a rank
				}
				if (!reader.get(10).isEmpty()) {
					long federalStateId = this.federalStates.get(reader.get(9))
							.getFederalStateId();
					this.listCandidates2005
							.add(new CsvListCandidate(prefixedId, names[0],
									names[1], reader.get(2), this.getPartyId(
											ElectionYear._2005, partyName),
									ElectionYear._2005, federalStateId, reader
											.get(10)));
				}
			}
			this.createLeftOverDirectCandidates(ElectionYear._2005);
			reader.close();
		} catch (FileNotFoundException ex) {
			if (reader != null) {
				reader.close();
			}
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_CANDIDATES_2005), ex);
		} catch (IOException ex) {
			if (reader != null) {
				reader.close();
			}
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_CANDIDATES_2005), ex);
		}
	}

	public Collection<CsvDirectCandidate> parseDirectCandidates2009()
			throws CsvParserException {
		parseCandidates2009();
		return this.directCandidates2009;
	}

	public Collection<CsvListCandidate> parseListCandidates2009()
			throws CsvParserException {
		parseCandidates2009();
		return this.listCandidates2009;

	}

	private void parseCandidates2009() throws CsvParserException {
		// already parsed?
		if (!this.directCandidates2009.isEmpty()
				&& !this.listCandidates2009.isEmpty()) {
			return;
		} else {
			// just a safety measure
			this.directCandidates2009.clear();
			this.listCandidates2009.clear();
		}
		parseDistrictsAndStates();
		if (this.partyIds.get(ElectionYear._2005).isEmpty()) {
			parseParties();
		}
		String partyName;
		long id = 1;
		long prefixedId;
		CsvReader reader = buildCsvReader(FILE_PATH_CANDIDATES_2009);
		try {
			reader.readHeaders();
			while (reader.readRecord()) {
				partyName = (this.partyNameReplacements.get(reader.get(3)) == null)
						? reader.get(3)
						: this.partyNameReplacements.get(reader.get(3));
				prefixedId = Long.parseLong(ElectionYear._2009.getPrefix() + ""
						+ id++);
				// direct candidate has electoral district
				if (!reader.get(4).isEmpty()) {
					this.directCandidates2009.add(new CsvDirectCandidate(
							prefixedId, reader.get(0), reader.get(1), reader
									.get(2), this.getPartyId(
									ElectionYear._2009, partyName),
							ElectionYear._2009, reader.get(4)));

				}
				//list candidate has rank
				if (!reader.get(6).isEmpty()) {
					long federalStateId = this.federalStates.get(reader.get(5))
							.getFederalStateId();
					this.listCandidates2009.add(new CsvListCandidate(
							prefixedId, reader.get(0), reader.get(1), reader
									.get(2), this.getPartyId(
									ElectionYear._2009, partyName),
							ElectionYear._2009, federalStateId, reader.get(6)));
				}
			}
			this.createLeftOverDirectCandidates(ElectionYear._2009);
			reader.close();
		} catch (FileNotFoundException ex) {
			if (reader != null) {
				reader.close();
			}
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_CANDIDATES_2005), ex);
		} catch (IOException ex) {
			if (reader != null) {
				reader.close();
			}
			throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file",
					FILE_PATH_CANDIDATES_2005), ex);
		}
	}

	private void createLeftOverDirectCandidates(ElectionYear year) {
		long id = 1;
		for (CsvElectoralDistrict dis : this.electoralDistricts) {
			long prefixedId = Long.parseLong(year.getPrefix() + "0" + id++);
			if (year.equals(ElectionYear._2005)) {
				this.directCandidates2005
						.add(new CsvDirectCandidate(prefixedId, "Uebrig",
								"Uebrig", year.toCleanString(), 50L, year, Long
										.toString(dis.getElectoralDistrictId())));
			} else {

				this.directCandidates2009
						.add(new CsvDirectCandidate(prefixedId, "Uebrig",
								"Uebrig", year.toCleanString(), 90L, year, Long
										.toString(dis.getElectoralDistrictId())));
			}
		}
	}
	private CsvReader buildCsvReader(String filePath) throws CsvParserException {
		return new CsvReader(new InputStreamReader(getClass()
				.getResourceAsStream(filePath)), CSV_ENTRY_DELIMITER);
	}

	@Override
	public Collection<CsvElection> parseElections() {
		List<CsvElection> elections = new ArrayList<CsvElection>();
		elections.add(new CsvElection(ElectionYear._2005));
		elections.add(new CsvElection(ElectionYear._2009));
		return elections;
	}
}
