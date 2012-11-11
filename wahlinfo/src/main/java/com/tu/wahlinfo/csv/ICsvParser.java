package com.tu.wahlinfo.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder.In;

import com.tu.wahlinfo.csv.impl.CsvParserException;
import com.tu.wahlinfo.csv.impl.helper.CandidateType;
import com.tu.wahlinfo.csv.impl.helper.ElectionYear;


/**
 *
 * @author cg
 */
public interface ICsvParser {

    /**
     * Parses all votes from an internally available csv into relational lists. Regardless of the specified year, both are parse due to the
     * internal structure of the csv. The parser expects the affiliation decodeable from at least the first (per header set as described 
     * below) and thereby the following header structure: <ul> <li> 0: Electoral district id <li> 1: Either electroal district name or 
     * federal state name <li> 3: All possible votes 2009 <li> 4: All possible votes 2005 <li> 7: Submitted votes 2009 <li> 8: Submitted 
     * votes 2005 <li> 11-end: Sets of 4 element: direct votes 2009, direct votes 2005, list votes 2009, list votes 2005 <ul>
     *
     * @param year
     * @param startElId
     * @param endELId
     * @return 
     * @throws CsvParserException In case the respective file can't be accessed/ read from.
     * @throws In case the provided year is not supported
     */
    public Map<String, List<String>> parseVotes(ElectionYear year, int startElId, int endELId) throws CsvParserException, IllegalArgumentException;

    /**
     * Expected/ relevant header structure: <ul> <li> 0: federal state name <li> 1: electoral district id <li> 2: electoral district name
     * <ul>
     *
     * @param year
     * @throws CsvParserException In case the respective file can't be accessed/ read from.
     * @throws IllegalArgumentException In case the provided year is not supported.
     */
    public Map<String, ArrayList<String>> parseElectoralDistricts(ElectionYear year) throws CsvParserException, IllegalArgumentException;

    /**
     * Extracts all candidates of the specified type and election year 2005 from the internally available csv file. Regardless of the actual
     * type, all candidates are parsed due to the underlying file structure. All candidates of the non-specified type are thus internally
     * stored for later access. Expected/ relevant header structure 2005: <ul> <li> 0: Full Name (will be splitted) <li> 2: Year of birth
     * <li> 8: Party <li> 9: Electoral district id <li> 10: Federal state <li> 11: Federal state list rank <ul>
     *
     * @param type Direct or list candidate.
     * @return A map containing relational column values (e.g. name, profession) as key and a list of all entries of that colum as value
     * @throws CsvParserException In case the respective file can't be accessed/ read from.
     * @throws IllegalArgumentException In case the provided candidate type is not supported.
     */
    public Map<String, List<String>> parseCandidates2005(CandidateType type) throws CsvParserException, IllegalArgumentException;

    /**
     * Extracts all candidates of the specified type and election year 2009 the internally available csv file. Regardless of the actual
     * type, all candidates are parsed due to the underlying file structure. All candidates of the non-specified type are thus internally
     * stored for later access. Expected/ relevant header structure 2005: <ul> <li> 0: First name <li> 1: Surname <li> 2: Year of birth <li>
     * 3: Party <li> 4: Electoral district id <li> 5: Federal state <li> 6 : Federal state list rank <ul>
     *
     * @param type Direct or list candidate.
     * @return A map containing relational column values (e.g. name, profession) as key and a list of all entries of that colum as value
     * @throws CsvParserException In case the respective file can't be accessed/ read from.
     * @throws IllegalArgumentException In case the provided candidate type is not supported.
     */
    public Map<String, List<String>> parseCandidates2009(CandidateType type) throws CsvParserException, IllegalArgumentException;
}
