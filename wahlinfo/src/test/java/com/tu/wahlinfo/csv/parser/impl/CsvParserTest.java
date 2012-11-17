package com.tu.wahlinfo.csv.parser.impl;

import java.util.Collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.csvreader.CsvReader;
import com.tu.wahlinfo.csv.entities.impl.CsvDirectCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvElectoralDistrict;
import com.tu.wahlinfo.csv.entities.impl.CsvFederalState;
import com.tu.wahlinfo.csv.entities.impl.CsvGeneratedVote;
import com.tu.wahlinfo.csv.entities.impl.CsvListCandidate;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;

public class CsvParserTest {

    private CsvParser parser;

    @Before
    public void setUp() {
	this.parser = new CsvParser();

    }

    @Test
    @Ignore
    public void stupidDistrictsAndStatesTest() throws Exception {
	Collection<CsvElectoralDistrict> districts = parser.parseElectoralDistricts();
	Collection<CsvFederalState> states = parser.parseFederalStates();

	for (CsvFederalState st : states) {
	    String out = st.getFederalStateId() + "\t" + st.getName();
	    System.out.println(out);
	}

	System.out.println();
	System.out.println("------------------------");
	System.out.println();

	for (CsvElectoralDistrict dis : districts) {
	    String out = dis.getElectoralDistrictId() + "\t" + dis.getElectoralDistrictName() + "\t" + dis.getFederalStateId();
	    System.out.println(out);
	}
    }

    @Test
    @Ignore
    public void stupidVotesTest() throws Exception {
	Collection<CsvVoteAggregation> voteAGs = parser.parseVoteAggregations(1, 1);
	Collection<CsvGeneratedVote> votes = parser.generateVotes(voteAGs.toArray(new CsvVoteAggregation[0])[0], 0);

	long counter1 = 0;
	long counter2 = 0;
	for (CsvGeneratedVote vote : votes) {
	    String out = vote.getTmpId() + "\t" + vote.getElectoralDistrictId() + "\t" + vote.getDirectVoteParty() + "\t" + vote.getListVoteParty();
	    if (vote.getDirectVoteParty().equals("NPD")) {
		counter1++;
	    }
	    if (vote.getListVoteParty().equals("NPD")) {
		counter2++;
	    }
	    System.out.println(out);
	}
	System.out.println(counter1 + " | " + counter2);

	CsvReader reader = new CsvReader("src/main/resources/csv/AllVotes.csv", ';');
	reader.readHeaders();
	reader.readRecord();
	System.out.println("checksum");
	System.out.println(reader.get("NPD_Erst_2009") + " | " + reader.get("NPD_Erst_2005"));
	System.out.println(reader.get("NPD_Zweit_2009") + " | " + reader.get("NPD_Zweit_2005"));

    }

    @Test
    @Ignore
    public void stupidCandidatesTest() throws Exception {
	Collection<CsvDirectCandidate> dcs = parser.parseDirectCandidates2005();
	Collection<CsvListCandidate> lcs = parser.parseListCandidates2005();

	for (CsvDirectCandidate dc : dcs) {
	    String out = dc.getSurname() + "\t" + dc.getYearOfBirth() + "\t" + dc.getParty() + "\t" + dc.getElectoralDistrictId();
	    System.out.println(out);
	}

	System.out.println();
	System.out.println("------------------------------------------------------------");
	System.out.println();
	System.out.println();
	System.out.println("----------------------------------------------------------");
	System.out.println();

	for (CsvListCandidate lc : lcs) {
	    String out = lc.getSurname() + "\t" + lc.getYearOfBirth() + "\t" + lc.getParty() + "\t" + lc.getFederalStateId() + "\t"
		    + lc.getPartyListRank();
	    System.out.println(out);
	}

	System.out.println("dc: " + dcs.size() + " | " + "lc: " + lcs.size());

    }

    @Test
    @Ignore
    public void tmpTest() throws Exception {

	CsvReader reader = new CsvReader("src/main/resources/csv/Candidates2005.csv", ';');
	reader.readHeaders();
	reader.readRecord();

	System.out.println("2: " + reader.get(2));
	System.out.println("8: " + reader.get(8));
	System.out.println("9: " + reader.get(9));

    }
}
