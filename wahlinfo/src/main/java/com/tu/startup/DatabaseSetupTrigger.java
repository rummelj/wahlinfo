package com.tu.startup;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabaseSetup;

@Singleton
@Startup
public class DatabaseSetupTrigger {

	@Inject
	DatabaseSetup databaseSetup;

	@PostConstruct
	public void init() throws DatabaseException {
		// TODO: Fix
		// databaseSetup.setup();
	}

}
