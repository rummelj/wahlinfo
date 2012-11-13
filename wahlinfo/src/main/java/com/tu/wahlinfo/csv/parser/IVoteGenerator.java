package com.tu.wahlinfo.csv.parser;

import java.util.Collection;

import com.tu.wahlinfo.csv.entities.impl.CsvGeneratedVote;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;

/**
 * 
 * @author cg
 */
public interface IVoteGenerator {

    /**
     * Generates single votes. Each vote must receive a shadow id to prevent
     * equals() collisions. Internally, vote of 2005 and 2009 are generated
     * separately and then combined.
     * 
     * @param aggregation
     * @param shadowIdStart
     * @return
     */
    public Collection<CsvGeneratedVote> generateVotes(CsvVoteAggregation aggregation, long shadowIdStart);

}
