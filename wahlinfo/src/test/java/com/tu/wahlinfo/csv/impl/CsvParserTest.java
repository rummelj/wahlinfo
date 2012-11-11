package com.tu.wahlinfo.csv.impl;

import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.tu.wahlinfo.csv.impl.helper.ElectionYear;
import com.tu.wahlinfo.csv.impl.helper.RelationalColumn;

public class CsvParserTest {
	
	private CsvParser parser;

    @Before
    public void setUp() {
        parser = new CsvParser();
    }

    /**
     * Test of parseVotes method, of class CsvParser.
     */
    @Test       
    public void testParseVotes() throws Exception {
        Map<String, List<String>> votes2005 = parser.parseVotes(ElectionYear._2005, 0, 0);

        List<String> elids = votes2005.get(RelationalColumn.EL_ID.toColumnString());
        List<String> parties = votes2005.get(RelationalColumn.PARTY.toColumnString());
        List<String> directVotes = votes2005.get(RelationalColumn.D_Votes.toColumnString());
        List<String> listVotes = votes2005.get(RelationalColumn.L_Votes.toColumnString());

        for (int k = 0; k < elids.size(); k++) {
            System.out.print(elids.get(k) + "\t" + parties.get(k)
                    + "\t" + directVotes.get(k) + "\t" + listVotes.get(k) + "\n");
        }

    }
    
    @Test
    @Ignore
    public void testCsvFileAcces() throws Exception {
        FileReader reader = new FileReader("src/main/resources/csv/AllVotes.csv");
        Assert.assertNotNull(reader);
        reader.close();
    }

}
