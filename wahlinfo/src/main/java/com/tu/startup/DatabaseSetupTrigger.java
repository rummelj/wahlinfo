package com.tu.startup;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabaseSetup;

@Stateless
public class DatabaseSetupTrigger {

	@Inject
	DatabaseSetup databaseSetup;

	public void init() {
		try {
			databaseSetup.setup();
		} catch (DatabaseException e) {
			// TODO Do logging
		}
	}

}
