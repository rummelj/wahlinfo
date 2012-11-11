package com.tu.wahlinfo.csv;

import java.util.List;
import java.util.Map;

/**
 *
 * @author cg
 */
public interface IVoteGenerator {

    Map<String, List<String>> generateVotes(String electoralDistrictId, List<String> parties, List<String> directVotes, List<String> listVotes);
    
}
