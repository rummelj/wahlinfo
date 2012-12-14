package com.tu.wahlinfo.csv.parser.impl;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.csvreader.CsvReader;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.CsvDirectCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvElectoralDistrict;
import com.tu.wahlinfo.csv.entities.impl.CsvFederalState;
import com.tu.wahlinfo.csv.entities.impl.CsvListCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvParty;

public class CsvParserTest {

	private CsvParser parser;

	@Before
	public void setUp() {
		this.parser = new CsvParser();
	}

	@Test
	public void testDirectCandidatesUniqueIds() throws CsvParserException {
		Collection<CsvDirectCandidate> candidates2005 = parser
				.parseDirectCandidates2005();
		Collection<CsvDirectCandidate> candidates2009 = parser
				.parseDirectCandidates2009();
		Set<Long> seenIds = new HashSet<Long>();
		for (CsvDirectCandidate candidate : candidates2005) {
			assertFalse("Id " + candidate.getId() + " appears more than once.",
					seenIds.contains(candidate.getId()));
			seenIds.add(candidate.getId());
		}
		for (CsvDirectCandidate candidate : candidates2009) {
			assertFalse("Id " + candidate.getId() + " appears more than once.",
					seenIds.contains(candidate.getId()));
			seenIds.add(candidate.getId());
		}
	}

	@Test
	@Ignore
	public void testParseParties() throws Exception {
		Collection<CsvParty> parties = parser.parseParties();
		for (CsvParty st : parties) {
			String out = st.getPartyId() + "\t" + st.getPartyName();
			System.out.println(out);
		}
	}

	@Test
	@Ignore
	public void stupidDistrictsAndStatesTest() throws Exception {
		Collection<CsvElectoralDistrict> districts = parser
				.parseElectoralDistricts();
		Collection<CsvFederalState> states = parser.parseFederalStates();

		for (CsvFederalState st : states) {
			String out = st.getFederalStateId() + "\t" + st.getName();
			System.out.println(out);
		}

		System.out.println();
		System.out.println("------------------------");
		System.out.println();

		for (CsvElectoralDistrict dis : districts) {
			String out = dis.getElectoralDistrictId() + "\t"
					+ dis.getElectoralDistrictName() + "\t"
					+ dis.getFederalStateId();
			System.out.println(out);
		}
	}

	@Test
	@Ignore
	public void stupidCandidatesTest() throws Exception {
		Collection<CsvDirectCandidate> dcs = parser.parseDirectCandidates2009();
		Collection<CsvListCandidate> lcs = parser.parseListCandidates2009();

		for (CsvDirectCandidate dc : dcs) {
			String out = dc.getId() + "\t" + dc.getPartyId() + "\t"
					+ dc.getElectoralDistrictId() + "\t"
					+ dc.getCandidatureYear();
			System.out.println(out);
		}

		System.out.println();
		System.out
				.println("------------------------------------------------------------");
		System.out.println();
		System.out.println();
		System.out
				.println("----------------------------------------------------------");
		System.out.println();

		for (CsvListCandidate lc : lcs) {
			String out = lc.getId() + "\t" + lc.getPartyId() + "\t"
					+ lc.getFederalStateId() + "\t" + lc.getPartyListRank()
					+ "\t" + lc.getCandidatureYear();
			System.out.println(out);
		}

		System.out.println("dc: " + dcs.size() + " | " + "lc: " + lcs.size());

	}

	@Test
	@Ignore
	public void tmpTest() throws Exception {

		CsvReader reader = new CsvReader(
				"src/main/resources/csv/Candidates2005.csv", ';');
		reader.readHeaders();
		reader.readRecord();

		System.out.println("2: " + reader.get(2));
		System.out.println("8: " + reader.get(8));
		System.out.println("9: " + reader.get(9));

	}
}
