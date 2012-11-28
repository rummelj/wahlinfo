package com.tu.wahlinfo.frontend.model;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.tu.common.Observable;
import com.tu.common.Observer;
import com.tu.wahlinfo.csv.CsvParserException;
import com.tu.wahlinfo.csv.parser.ICsvToDatabaseSyncer;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
@SessionScoped
public class SyncModel implements Observer {

	int progress;

	@Inject
	ICsvToDatabaseSyncer csvToDatabaseSyncer;

	@PostConstruct
	public void init() {
		csvToDatabaseSyncer.register(this);
	}

	public void launchSync() throws CsvParserException, DatabaseException {
		csvToDatabaseSyncer.sync();
	}

	@Override
	public void notify(Observable observable) {
		progress = ((ICsvToDatabaseSyncer) observable).getProgressInPercent();
	}

	public int getProgress() {
		return csvToDatabaseSyncer.getProgressInPercent();
	}
}
