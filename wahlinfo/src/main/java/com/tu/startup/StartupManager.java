package com.tu.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class StartupManager {

	@Inject
	DatabaseSetupTrigger databaseSetupTrigger;

	@Inject
	CsvToDatabaseSyncerTrigger csvToDatabaseSyncerTrigger;

	@PostConstruct
	public void init() {
		databaseSetupTrigger.init();
		csvToDatabaseSyncerTrigger.init();
	}

}
