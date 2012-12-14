package com.tu.wahlinfo.csv.parser;

import java.io.File;

import com.tu.wahlinfo.csv.CsvParserException;

public abstract class AbstractVoteFileGenerator {

	public static final String OUTPUT_FILE_PREFIX = "genVotes";
	protected ICsvParser parser;

	public AbstractVoteFileGenerator(ICsvParser parser) {
		super();
		this.parser = parser;
	}

	public abstract File[] createVoteFiles() throws CsvParserException;

}
