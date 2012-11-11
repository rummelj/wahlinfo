package com.tu.wahlinfo.csv.impl;

import com.tu.wahlinfo.csv.IVoteGenerator;
import com.tu.wahlinfo.csv.impl.helper.RelationalColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author cg
 */
public class VoteGenerator implements IVoteGenerator{

    public Map<String, List<String>> generateVotes(String electoralDistrictId, List<String> parties, List<String> directVotes, List<String> listVotes) {
        Map<String, List<String>> entries = new TreeMap<String, List<String>>();

        List<String> voteIds = new ArrayList<String>();
        List<String> voteDirectParties = new ArrayList<String>();
        List<String> voteListParties = new ArrayList<String>();
        long id = 0;

        int dirVotesIndex = 0;
        int listVotesIndex = 0;

        int numDirVotes = parseEntry(directVotes.get(0));
        int numListVotes = parseEntry(listVotes.get(0));

        while (true) {
        	//decrease vote number, fetch from new party and update party index, or flag as out of votes (-1)
            while (numDirVotes == 0 && dirVotesIndex < directVotes.size() -1) {
                numDirVotes = parseEntry(directVotes.get(++dirVotesIndex));
            }
            // if num = 0 -> abort condition, else decrease for next entry
            numDirVotes--;
            
            while(numListVotes == 0 && listVotesIndex < listVotes.size()-1){
                numListVotes = parseEntry(listVotes.get(++listVotesIndex));
            }
            // if num = 0 -> abort condition, else decrease for next entry
            numListVotes--;
            
            // no more votes left -> terminate
            if (numDirVotes == -1 && numListVotes == -1) {
                break;
            } else {
                voteIds.add(Long.toString(id++));
                // no more direct, but list votes in district means partially invalid votes
                voteDirectParties.add((numDirVotes != -1) ? parties.get(dirVotesIndex) : "");
                // no more list, but direct votes in district means partially invalid votes
                voteListParties.add((numListVotes != -1) ? parties.get(listVotesIndex) : "");

            }

        }
        entries.put(RelationalColumn.V_ID.toColumnString(), voteIds);
        entries.put(RelationalColumn.D_VOTE_PARTY.toColumnString(), voteDirectParties);
        entries.put(RelationalColumn.L_VOTE_PARTY.toColumnString(), voteListParties);

        return entries;
    }
    
    private int parseEntry(String entry){
        if(entry.equals("")){
            return 0;
        } else {
            return Integer.parseInt(entry);
        }
    }
}
