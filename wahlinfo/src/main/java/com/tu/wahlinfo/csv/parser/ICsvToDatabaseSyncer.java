package com.tu.wahlinfo.csv.parser;

import java.io.File;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.persistence.DatabaseException;

public interface ICsvToDatabaseSyncer {

	/**
	 * Processes all csv entities and stores them into the database.
	 * 
	 * @throws CsvParserException
	 * @throws DatabaseException
	 */
	File[] sync() throws CsvParserException, DatabaseException;

	/**
	 * Write votes from files to db
	 * 
	 * @throws CsvParserException
	 * @throws DatabaseException
	 */
	void sync2(File file, int k) throws CsvParserException, DatabaseException;

}
