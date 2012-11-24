package com.tu.wahlinfo.csv.parser.impl;

import java.util.Collection;
import java.util.HashSet;

import com.tu.wahlinfo.csv.entities.impl.CsvGeneratedVote;
import com.tu.wahlinfo.csv.entities.impl.CsvPartialVoteAggregation;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.csv.parser.IVoteGenerator;

/**
 * 
 * @author cg
 */
public class VoteGenerator implements IVoteGenerator {

    /**
     * Generates single votes out of a vote aggregation. 
     * @param aggregation
     * @param shadowIdStart Required to prevent hash collisions between otherwise identical votes.    
     * @return
     */
    public Collection<CsvGeneratedVote> generateVotes(CsvVoteAggregation aggregation, long shadowIdStart) {
	Collection<CsvGeneratedVote> part1 = generateVotes(aggregation, shadowIdStart, ElectionYear._2005);
	Collection<CsvGeneratedVote> part2 = generateVotes(aggregation, part1.size(), ElectionYear._2009);
	part1.addAll(part2);
	return part1;
    }
   
    private Collection<CsvGeneratedVote> generateVotes(CsvVoteAggregation aggregation, long shadowIdStart, ElectionYear year) {
	Collection<CsvGeneratedVote> votes = new HashSet<CsvGeneratedVote>();

	CsvPartialVoteAggregation[] partialAggregations = aggregation.getPartialAggregations().toArray(new CsvPartialVoteAggregation[0]);
	long id = shadowIdStart;
	int dirVotesIndex = 0;
	int listVotesIndex = 0;

	long numDirVotes = partialAggregations[0].getNumDirectVotes(year);
	long numListVotes = partialAggregations[0].getNumListVotes(year);

	while (true) {
	    // decrease vote number, fetch from new party and update party
	    // index, or flag as out of votes (-1)
	    while (numDirVotes == 0 && dirVotesIndex < partialAggregations.length - 1) {
		numDirVotes = (partialAggregations[++dirVotesIndex]).getNumDirectVotes(year);
	    }
	    // if num = 0 -> abort condition, else decrease for next entry
	    numDirVotes--;

	    while (numListVotes == 0 && listVotesIndex < partialAggregations.length - 1) {
		numListVotes = (partialAggregations[++listVotesIndex]).getNumListVotes(year);
	    }
	    // if num = 0 -> abort condition, else decrease for next entry
	    numListVotes--;

	    // no more votes left -> terminate
	    if (numDirVotes == -1 && numListVotes == -1) {
		break;
	    } else {
		votes.add(new CsvGeneratedVote(id++, aggregation.getElectoralDistrictId(), partialAggregations[dirVotesIndex].getDirectCanidateId(year),
			partialAggregations[listVotesIndex].getPartyId(year)));
	    }
	}
	return votes;
    }
}
