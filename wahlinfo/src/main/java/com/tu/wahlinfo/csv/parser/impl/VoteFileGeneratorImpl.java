package com.tu.wahlinfo.csv.parser.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.entities.impl.CsvPartialVoteAggregation;
import com.tu.wahlinfo.csv.entities.impl.CsvVoteAggregation;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.csv.parser.AbstractVoteFileGenerator;
import com.tu.wahlinfo.csv.parser.ICsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteFileGeneratorImpl extends AbstractVoteFileGenerator {

	private static final String CSV_FILE_DELIMITER = ";";
	private static final String OUTPUT_FILE_TYPE = ".csv";
        private static final Logger LOG = LoggerFactory.getLogger(VoteFileGeneratorImpl.class);

	public VoteFileGeneratorImpl(ICsvParser parser) {
		super(parser);
		// TODO Auto-generated constructor stub
	}

	@Override
	public File[] createVoteFiles() throws CsvParserException {
		File[] res = new File[5];
		try {
                    
			res[0] = createOutputFile(1);
			res[1] = createOutputFile(2);
			res[2] = createOutputFile(3);
			res[3] = createOutputFile(4);
			res[4] = createOutputFile(5);
			writeToFile(0, 59, res[0]);
			writeToFile(60, 119, res[1]);
			writeToFile(120, 179, res[2]);
			writeToFile(180, 239, res[3]);
			writeToFile(240, 300, res[4]);
                        
                        
			return res;
		} catch (Exception ex) {
			throw new CsvParserException("fail", ex);
		}

	}
	void writeToFile(int startRowNumber, int endRowNumber, File outputFile)
			throws IOException, CsvParserException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		Collection<CsvVoteAggregation> tmp = this.parser.parseVoteAggregations(
				startRowNumber, endRowNumber);
		for (CsvVoteAggregation va : tmp) {
			writeVotes(va, ElectionYear._2005, writer);
			writeVotes(va, ElectionYear._2009, writer);

		}
		writer.close();
	}

	void writeVotes(CsvVoteAggregation aggregation, ElectionYear year,
			BufferedWriter writer) throws IOException {

		CsvPartialVoteAggregation[] partialAggregations = aggregation
				.getPartialAggregations().toArray(
						new CsvPartialVoteAggregation[0]);

		int dirVotesIndex = 0;
		int listVotesIndex = 0;

		long numDirVotes = partialAggregations[0].getNumDirectVotes(year);
		long numListVotes = partialAggregations[0].getNumListVotes(year);

		while (true) {
			// decrease vote number, fetch from new party and update party
			// index, or flag as out of votes (-1)
			while (numDirVotes == 0
					&& dirVotesIndex < partialAggregations.length - 1) {
				numDirVotes = (partialAggregations[++dirVotesIndex])
						.getNumDirectVotes(year);
			}
			// if num = 0 -> abort condition, else decrease for next entry
			numDirVotes--;

			while (numListVotes == 0
					&& listVotesIndex < partialAggregations.length - 1) {
				numListVotes = (partialAggregations[++listVotesIndex])
						.getNumListVotes(year);
			}
			// if num = 0 -> abort condition, else decrease for next entry
			numListVotes--;

			// no more votes left -> terminate
			if (numDirVotes == -1 && numListVotes == -1) {
				break;
			} else {
				writer.write(constructCsvLine(aggregation
						.getElectoralDistrictId(),
						partialAggregations[dirVotesIndex]
								.getDirectCanidateId(year),
						partialAggregations[listVotesIndex].getPartyId(year)));
				writer.newLine();
			}
		}
	}

	String constructCsvLine(Long... cells) {
		StringBuilder builder = new StringBuilder();		
		for (int k=0;k<cells.length;k++) {
			builder.append((cells[k] == null) ? "" : cells[k]);
			if(k<cells.length-1){
				builder.append(CSV_FILE_DELIMITER);
			}
		}
		return builder.toString();
	}

	File createOutputFile(int number) throws IOException {
                
		File file = File.createTempFile(OUTPUT_FILE_PREFIX + number,OUTPUT_FILE_TYPE);
		LOG.info("Created file: " + file.getAbsolutePath());
		return file;
	}

}
