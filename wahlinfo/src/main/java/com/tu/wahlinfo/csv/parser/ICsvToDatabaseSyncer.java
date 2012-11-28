package com.tu.wahlinfo.csv.parser;

import com.tu.common.Observable;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.persistence.DatabaseException;

public interface ICsvToDatabaseSyncer extends Observable {

	/**
	 * Processes all csv entities and stores them into the database.
	 * 
	 * @throws CsvParserException
	 * @throws DatabaseException
	 */
	void sync() throws CsvParserException, DatabaseException;

	/**
	 * Returns the progress of a running sync.
	 */
	int getProgressInPercent();
}
