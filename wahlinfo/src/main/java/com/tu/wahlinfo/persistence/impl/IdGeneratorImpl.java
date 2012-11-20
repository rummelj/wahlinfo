package com.tu.wahlinfo.persistence.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.persistence.IdGenerator;

/**
 * @author Johannes
 * 
 */
@Stateless
public class IdGeneratorImpl implements IdGenerator {

	@Inject
	DatabaseAccessor databaseAccessor;

	/**
	 * 
	 */
	public IdGeneratorImpl() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tu.wahlinfo.persistence.IdGenerator#getId()
	 */
	@Override
	public long getId() throws DatabaseException {
		Map<String, List<String>> hibernate_sequence = databaseAccessor
				.executeQuery("SELECT * FROM hibernate_sequence", "next_val");
		Long currentValue = Long.valueOf(hibernate_sequence.get("next_val")
				.get(0));
		Long nextValue = currentValue + 1;
		databaseAccessor
				.executeStatement("UPDATE hibernate_sequence SET next_val="
						+ nextValue + " WHERE next_val=" + currentValue + ";");
		return nextValue;
	}

}
