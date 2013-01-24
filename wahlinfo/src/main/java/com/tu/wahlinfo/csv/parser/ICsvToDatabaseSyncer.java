package com.tu.wahlinfo.csv.parser;

import java.io.File;

import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.persistence.DatabaseException;

public interface ICsvToDatabaseSyncer {

	/**
	 * Processes all csv entities and stores them into the database.
	 * 
	 * @param is
	 *            server environment is glassfish
	 * 
	 * @throws CsvParserException
	 * @throws DatabaseException
	 */
	File[] sync(boolean isGfEnv) throws CsvParserException, DatabaseException;

	/**
	 * Write votes from files to db. Expects exactly 5 files (bad impl i
	 * know...)
	 * 
	 * @throws CsvParserException
	 * @throws DatabaseException
	 */
	void sync2(File[] files) throws CsvParserException, DatabaseException;

}
