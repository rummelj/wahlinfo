package com.tu.wahlinfo.analysis.impl;

import com.tu.util.FileScanner;
import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.DirectCandidate;
import com.tu.wahlinfo.frontend.model.FederalState;
import com.tu.wahlinfo.frontend.model.Party;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class VoteAnalysisImpl implements IVoteAnalysis {

    private static final String FILE_PATH_SQL_BASE = "/sql/";
    private static final String FILE_PATH_DIR_VOTE_AGGREGATION_SCRIPT =
            FILE_PATH_SQL_BASE + "aggregateDirCanVotes.sql";
    private static final String FILE_PATH_LIST_VOTE_AGGREGATION_SCRIPT =
            FILE_PATH_SQL_BASE + "aggregatePartyVotes.sql";
    private static final Logger LOG = LoggerFactory
            .getLogger(VoteAnalysisImpl.class);
    @Inject
    DatabaseAccessor databaseAccessor;

    @Override
    public void updateVoteBase() throws DatabaseException {
        try {
            LOG.info("Starting vote base update");
            LOG.debug("Updating party votes");
            databaseAccessor.executeStatement(FileScanner
                    .scanFile(FILE_PATH_LIST_VOTE_AGGREGATION_SCRIPT));
            LOG.debug("Done");

            LOG.debug("Updating direct candidate votes");
            databaseAccessor.executeStatement(FileScanner
                    .scanFile(FILE_PATH_DIR_VOTE_AGGREGATION_SCRIPT));            
            LOG.info("Vote update finished");
        } catch (IOException ex) {
            throw new DatabaseException(
                    "Unable to access files required for vote base update");
        }
    }

    @Override
    public List<DirectCandidate> getDirMandates(ElectionYear electionYear)
            throws DatabaseException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List getListMandates(ElectionYear electionYear)
            throws DatabaseException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Party, Integer> getOverhangMandates(ElectionYear electionYear)
            throws DatabaseException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Party, Integer> getSeatDistribution(ElectionYear electionYear)
            throws DatabaseException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<FederalState, Integer> getStateSeatDistribution(
            ElectionYear electionYear, String partyName)
            throws DatabaseException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
