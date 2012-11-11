package com.tu.wahlinfo.csv.impl;

import com.csvreader.CsvReader;
import com.tu.wahlinfo.csv.ICsvParser;
import com.tu.wahlinfo.csv.impl.helper.CandidateType;
import com.tu.wahlinfo.csv.impl.helper.ElectionYear;
import com.tu.wahlinfo.csv.impl.helper.RelationalColumn;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author cg
 */
public class CsvParser implements ICsvParser {

    private final static String AFFILIATION_DELIMITER = "_";
    private final static String NAME_ENTRY_DELIMITER = ",";
    private final static char CSV_ENTRY_DELIMITER = ';';
    private final static String EL_EXCLUDE_PATTERN_2009 = "9\\d\\d";
    //Path logic
    private final static String FILE_PATH_BASE_CSV = "src/main/resources/csv/";
    private final static String FILE_PATH_VOTES = FILE_PATH_BASE_CSV + "AllVotes.csv";
    private final static String FILE_PATH_CANDIDATES_2005 = FILE_PATH_BASE_CSV + "Candidates2005.csv";
    private final static String FILE_PATH_CANDIDATES_2009 = FILE_PATH_BASE_CSV + "Candidates2009.csv";
    private final static String FILE_PATH_EL_DISTRICTS_2005 = FILE_PATH_BASE_CSV + "ElectoralDistricts2005.csv";
    private final static String FILE_PATH_EL_DISTRICTS_2009 = FILE_PATH_BASE_CSV + "ElectoralDistricts2009.csv";
    //Exception messages
    private final static String MSG_FILE_PATH_ERR = "Unable to open/read file [:file]";
    private final static String MSG_UNSUPPORTED_CANDIDATE_TYPE = "The specified candicate type [:type] is not supported";
    private final static String MSG_UNSUPPORTED_YEAR = "The specified year [:year] is not supported";
    //Candidate maps
    private Map<String, List<String>> directCandidates2005 = new TreeMap<String, List<String>>();
    private Map<String, List<String>> directCandidates2009 = new TreeMap<String, List<String>>();
    private Map<String, List<String>> listCandidates2005 = new TreeMap<String, List<String>>();
    private Map<String, List<String>> listCandidates2009 = new TreeMap<String, List<String>>();
    //VoteMaps
    private Map<String, List<String>> aggregatedVotes2005 = new TreeMap<String, List<String>>();
    private Map<String, List<String>> aggregatedVotes2009 = new TreeMap<String, List<String>>();

    public CsvParser() {
    }

    @Override
    public Map<String, List<String>> parseVotes(ElectionYear year, int startElId, int endELId) throws CsvParserException, IllegalArgumentException {
        if (year.equals(ElectionYear._2005)) {
            if (!aggregatedVotes2005.isEmpty()) {
                return aggregatedVotes2005;
            }
        } else if (year.equals(ElectionYear._2009)) {
            if (!aggregatedVotes2009.isEmpty()) {
                return aggregatedVotes2009;
            }
        } else {
            throw new IllegalArgumentException(MSG_UNSUPPORTED_YEAR.replace(":year", year.toCleanString()));
        }

        ArrayList<String> elDistrictIds2005 = new ArrayList<String>();
        ArrayList<String> parties2005 = new ArrayList<String>();
        ArrayList<String> numDirectVotes2005 = new ArrayList<String>();
        ArrayList<String> numListVotes2005 = new ArrayList<String>();

        ArrayList<String> elDistrictIds2009 = new ArrayList<String>();
        ArrayList<String> parties2009 = new ArrayList<String>();
        ArrayList<String> numDirectVotes2009 = new ArrayList<String>();
        ArrayList<String> numListVotes2009 = new ArrayList<String>();

        CsvReader reader = null;
        try {
            reader = buildCsvReader(FILE_PATH_VOTES);
            reader.readHeaders();
            while (reader.readRecord()) {
            	if (reader.get(2).equals("99") || reader.get(1).equals("Bundesgebiet") || 
                        Integer.valueOf(reader.get(0)).intValue() < startElId) {
                    continue;
                }
                if(Integer.valueOf(reader.get(0)).intValue() > endELId){
                    break;
                }
                for (int k = 11; k < reader.getColumnCount(); k += 4) {
                    int index = k;
                    if(index==15){
                        //skip entry for valid votes
                        continue;
                    }
                    elDistrictIds2005.add(reader.get(0));
                    parties2005.add(reader.getHeader(index).split(AFFILIATION_DELIMITER)[0]);
                    numDirectVotes2005.add(reader.get(index + 1));
                    numListVotes2005.add(reader.get(index + 3));

                    elDistrictIds2009.add(reader.get(0));
                    parties2009.add(reader.getHeader(index).split(AFFILIATION_DELIMITER)[0]);
                    numDirectVotes2009.add(reader.get(index));
                    numListVotes2009.add(reader.get(index + 2));
                }
            }

            aggregatedVotes2005.put(RelationalColumn.EL_ID.toColumnString(), elDistrictIds2005);
            aggregatedVotes2005.put(RelationalColumn.PARTY.toColumnString(), parties2005);
            aggregatedVotes2005.put(RelationalColumn.D_Votes.toColumnString(), numDirectVotes2005);
            aggregatedVotes2005.put(RelationalColumn.L_Votes.toColumnString(), numListVotes2005);

            aggregatedVotes2009.put(RelationalColumn.EL_ID.toColumnString(), elDistrictIds2009);
            aggregatedVotes2009.put(RelationalColumn.PARTY.toColumnString(), parties2009);
            aggregatedVotes2009.put(RelationalColumn.D_Votes.toColumnString(), numDirectVotes2009);
            aggregatedVotes2009.put(RelationalColumn.L_Votes.toColumnString(), numListVotes2009);

            reader.close();
            
            if (year.equals(ElectionYear._2005)) {
                return aggregatedVotes2005;
            } else if (year.equals(ElectionYear._2009)) {
                return aggregatedVotes2009;
            } else {
                return null;
            }

        } catch (FileNotFoundException ex) {
            if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_VOTES), ex);
        } catch (IOException ex) {
            if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_VOTES), ex);
        }
    }

    @Override
    public Map<String, List<String>> parseCandidates2005(CandidateType type) throws CsvParserException, IllegalArgumentException {
        if (type.equals(CandidateType.DIRECT)) {
            if (!directCandidates2005.isEmpty()) {
                return directCandidates2005;
            }
        } else if (type.equals(CandidateType.LIST)) {
            if (!listCandidates2005.isEmpty()) {
                return listCandidates2005;
            }
        } else {
            throw new IllegalArgumentException(MSG_UNSUPPORTED_CANDIDATE_TYPE.replace(":type", type.toString()));
        }

        List<String> directFirstNames = new ArrayList<String>();
        List<String> directSurNames = new ArrayList<String>();
        List<String> directYearsOfBirth = new ArrayList<String>();
        List<String> directParties = new ArrayList<String>();
        List<String> directElectoralDistrictIds = new ArrayList<String>();

        List<String> listFirstNames = new ArrayList<String>();
        List<String> listSurNames = new ArrayList<String>();
        List<String> listYearsOfBirth = new ArrayList<String>();
        List<String> listParties = new ArrayList<String>();
        List<String> listFederalStateNames = new ArrayList<String>();
        List<String> listRanks = new ArrayList<String>();

        CsvReader reader = null;
        try {
            reader = buildCsvReader(FILE_PATH_CANDIDATES_2005);
            reader.readHeaders();
            while (reader.readRecord()) {
                //direct candidate has no rank entry
                if (reader.get(11).isEmpty()) {
                    directFirstNames.add(reader.get(0).split(NAME_ENTRY_DELIMITER)[1].trim());
                    directSurNames.add(reader.get(0).split(NAME_ENTRY_DELIMITER)[0].trim());
                    directYearsOfBirth.add(reader.get(2));
                    directParties.add(reader.get(8));
                    directElectoralDistrictIds.add(reader.get(9));
                } else {
                    listFirstNames.add(reader.get(0).split(NAME_ENTRY_DELIMITER)[1].trim());
                    listSurNames.add(reader.get(0).split(NAME_ENTRY_DELIMITER)[0].trim());
                    listYearsOfBirth.add(reader.get(2));
                    listParties.add(reader.get(8));
                    listFederalStateNames.add(reader.get(10));
                    listRanks.add(reader.get(11));
                }
            }

            directCandidates2005.put(RelationalColumn.F_NAME.toColumnString(), directFirstNames);
            directCandidates2005.put(RelationalColumn.S_NAME.toColumnString(), directSurNames);
            directCandidates2005.put(RelationalColumn.YOB.toColumnString(), directYearsOfBirth);
            directCandidates2005.put(RelationalColumn.PARTY.toColumnString(), directParties);
            directCandidates2005.put(RelationalColumn.EL_ID.toColumnString(), directElectoralDistrictIds);

            listCandidates2005.put(RelationalColumn.F_NAME.toColumnString(), listFirstNames);
            listCandidates2005.put(RelationalColumn.S_NAME.toColumnString(), listSurNames);
            listCandidates2005.put(RelationalColumn.YOB.toColumnString(), listYearsOfBirth);
            listCandidates2005.put(RelationalColumn.PARTY.toColumnString(), listParties);
            listCandidates2005.put(RelationalColumn.FS_NAME.toColumnString(), listFederalStateNames);
            listCandidates2005.put(RelationalColumn.L_RANK.toColumnString(), listRanks);

            reader.close();
            
            if (type.equals(CandidateType.DIRECT)) {
                return directCandidates2005;
            } else if (type.equals(CandidateType.LIST)) {
                return listCandidates2005;
            } else {
                //exception should have been thrown before
                return null;
            }
        } catch (FileNotFoundException ex) {
             if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
        } catch (IOException ex) {
             if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
        }
    }

    @Override
    public Map<String, List<String>> parseCandidates2009(CandidateType type) throws CsvParserException, IllegalArgumentException {
        if (type.equals(CandidateType.DIRECT)) {
            if (!directCandidates2009.isEmpty()) {
                return directCandidates2009;
            }
        } else if (type.equals(CandidateType.LIST)) {
            if (!listCandidates2009.isEmpty()) {
                return listCandidates2009;
            }
        } else {
            throw new IllegalArgumentException(MSG_UNSUPPORTED_CANDIDATE_TYPE.replace(":type", type.toString()));
        }

        List<String> directFirstNames = new ArrayList<String>();
        List<String> directSurNames = new ArrayList<String>();
        List<String> directYearsOfBirth = new ArrayList<String>();
        List<String> directParties = new ArrayList<String>();
        List<String> directElectoralDistrictIds = new ArrayList<String>();

        List<String> listFirstNames = new ArrayList<String>();
        List<String> listSurNames = new ArrayList<String>();
        List<String> listYearsOfBirth = new ArrayList<String>();
        List<String> listParties = new ArrayList<String>();
        List<String> listFederalStateNames = new ArrayList<String>();
        List<String> listRanks = new ArrayList<String>();

        CsvReader reader = null;
        try {
            reader = buildCsvReader(FILE_PATH_CANDIDATES_2009);
            reader.readHeaders();
            while (reader.readRecord()) {
                //direct candidate has no rank entry
                if (reader.get(11).isEmpty()) {
                    directFirstNames.add(reader.get(0));
                    directSurNames.add(reader.get(1));
                    directYearsOfBirth.add(reader.get(2));
                    directParties.add(reader.get(3));
                    directElectoralDistrictIds.add(reader.get(4));
                } else {
                    listFirstNames.add(reader.get(0));
                    listSurNames.add(reader.get(1));
                    listYearsOfBirth.add(reader.get(2));
                    listParties.add(reader.get(3));
                    listFederalStateNames.add(reader.get(5));
                    listRanks.add(reader.get(6));
                }
            }

            directCandidates2009.put(RelationalColumn.F_NAME.toColumnString(), directFirstNames);
            directCandidates2009.put(RelationalColumn.S_NAME.toColumnString(), directSurNames);
            directCandidates2009.put(RelationalColumn.YOB.toColumnString(), directYearsOfBirth);
            directCandidates2009.put(RelationalColumn.PARTY.toColumnString(), directParties);
            directCandidates2009.put(RelationalColumn.EL_ID.toColumnString(), directElectoralDistrictIds);

            listCandidates2009.put(RelationalColumn.F_NAME.toColumnString(), listFirstNames);
            listCandidates2009.put(RelationalColumn.S_NAME.toColumnString(), listSurNames);
            listCandidates2009.put(RelationalColumn.YOB.toColumnString(), listYearsOfBirth);
            listCandidates2009.put(RelationalColumn.PARTY.toColumnString(), listParties);
            listCandidates2009.put(RelationalColumn.FS_NAME.toColumnString(), listFederalStateNames);
            listCandidates2009.put(RelationalColumn.L_RANK.toColumnString(), listRanks);

            reader.close();
            
            if (type.equals(CandidateType.DIRECT)) {
                return directCandidates2009;
            } else if (type.equals(CandidateType.LIST)) {
                return listCandidates2009;
            } else {
                //exception should have been thrown before
                return null;
            }
        } catch (FileNotFoundException ex) {
             if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
        } catch (IOException ex) {
             if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", FILE_PATH_CANDIDATES_2005), ex);
        }
    }

    @Override
    public Map<String, ArrayList<String>> parseElectoralDistricts(ElectionYear year) throws CsvParserException, IllegalArgumentException {
        String filePath;
        CsvReader reader;
        Map<String, ArrayList<String>> districts = new TreeMap<String, ArrayList<String>>();
        ArrayList<String> federalStateNames = new ArrayList<String>();
        ArrayList<String> districtIds = new ArrayList<String>();
        ArrayList<String> districtNames = new ArrayList<String>();

        if (year.equals(ElectionYear._2005)) {
            filePath = FILE_PATH_EL_DISTRICTS_2005;
        } else if (year.equals(ElectionYear._2009)) {
            filePath = FILE_PATH_EL_DISTRICTS_2009;
        } else {
            throw new IllegalArgumentException(MSG_UNSUPPORTED_YEAR.replace(":year", year.toCleanString()));
        }
        reader = buildCsvReader(filePath);
        try {
            reader.readHeaders();
            while (reader.readRecord()) {
                if (year.equals(ElectionYear._2005) && "0".equals(reader.get(1))) {
                    continue;
                } else if (year.equals(ElectionYear._2009) && reader.get(1).matches(EL_EXCLUDE_PATTERN_2009)) {
                    continue;
                }
                federalStateNames.add(reader.get(0));
                districtIds.add(reader.get(1));
                districtNames.add(reader.get((2)));
            }
            districts.put(RelationalColumn.FS_NAME.toColumnString(), federalStateNames);
            districts.put(RelationalColumn.EL_ID.toColumnString(), districtIds);
            districts.put(RelationalColumn.EL_NAME.toColumnString(), districtNames);
            reader.close();
            return districts;
        } catch (IOException ex) {
             if(reader!=null){
                 reader.close();
            }
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", filePath), ex);
        }
    }

    private CsvReader buildCsvReader(String filePath) throws CsvParserException {
        try {
            return new CsvReader(new FileReader(filePath), CSV_ENTRY_DELIMITER);
        } catch (FileNotFoundException fnfe) {
            throw new CsvParserException(MSG_FILE_PATH_ERR.replace(":file", filePath), fnfe);
        }
    }
}
