package com.tu.startup;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabasePersister;
import com.tu.wahlinfo.persistence.DatabaseSetup;

@Stateless
public class DatabaseSetupTrigger {

	@Inject
	DatabaseSetup databaseSetup;

	@Inject
	DatabasePersister databasePersister;

	public void init() {
		try {
			databaseSetup.setup();
		} catch (DatabaseException e) {
			// TODO Do logging
		}
	}

}
