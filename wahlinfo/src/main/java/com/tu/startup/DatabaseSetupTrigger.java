package com.tu.startup;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.DatabaseSetup;

@Stateless
public class DatabaseSetupTrigger {

	private final static Logger LOG = LoggerFactory
			.getLogger(DatabaseSetupTrigger.class);

	@Inject
	DatabaseSetup databaseSetup;

	public void init() {
		try {
			databaseSetup.setup();
		} catch (DatabaseException e) {
			LOG.error("", e);
		}
	}

}
