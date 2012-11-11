package com.tu.wahlinfo.csv.impl;

import com.tu.wahlinfo.csv.impl.helper.ElectionYear;
import com.tu.wahlinfo.csv.impl.helper.RelationalColumn;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author Cons
 */
public class VoteGeneratorTest {
    
    private CsvParser parser;
    private VoteGenerator generator;
    
    @Before
    public void setUp() {
        parser = new CsvParser();
        generator = new VoteGenerator();
    }
    
    /**
     * Test of generateVotes method, of class VoteGenerator.
     */
    @Test
//    @Ignore
    public void testGenerateVotes() throws Exception{
       Map<String,List<String>> result = parser.parseVotes(ElectionYear._2005, 1, 1);
       Map<String,List<String>> entries = generator.generateVotes(result.get(RelationalColumn.EL_ID.toColumnString()).get(0), 
               result.get(RelationalColumn.PARTY.toColumnString()), 
               result.get(RelationalColumn.D_Votes.toColumnString()),
               result.get(RelationalColumn.L_Votes.toColumnString()));
       
       List<String> voteIds = entries.get(RelationalColumn.V_ID.toColumnString());
        List<String> directParties = entries.get(RelationalColumn.D_VOTE_PARTY.toColumnString());
        List<String> listParties = entries.get(RelationalColumn.L_VOTE_PARTY.toColumnString());

        for (int k = 0; k < voteIds.size(); k++) {
            System.out.print(voteIds.get(k) + "\t" + directParties.get(k)
                    + "\t" + listParties.get(k) + "\n");
        }
       
    }
}
