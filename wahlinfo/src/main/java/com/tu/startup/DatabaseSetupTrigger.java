package com.tu.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.DatabaseSetup;

@Singleton
@Startup
public class DatabaseSetupTrigger {

	@Inject
	DatabaseSetup databaseSetup;

	@Inject
	DatabasePersister databasePersister;

	@PostConstruct
	public void init() throws DatabaseException {
		databaseSetup.setup();
	}

}
