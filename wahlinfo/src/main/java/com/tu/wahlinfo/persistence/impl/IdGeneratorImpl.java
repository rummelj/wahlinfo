package com.tu.wahlinfo.persistence.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.tu.wahlinfo.model.DatabaseResult;
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
		Long currentValue = getCurrentId();
		Long nextValue = currentValue + 1;
		setId(currentValue, nextValue);
		return nextValue;
	}

	private void setId(Long currentValue, Long nextValue)
			throws DatabaseException {
		databaseAccessor
				.executeStatement("UPDATE hibernate_sequence SET next_val="
						+ nextValue + " WHERE next_val=" + currentValue + ";");
	}

	private Long getCurrentId() throws DatabaseException {
		DatabaseResult hibernate_sequence = databaseAccessor.executeQuery(
				"SELECT * FROM hibernate_sequence", "next_val");
		Long currentValue = Long.valueOf(hibernate_sequence.iterator().next()
				.get("next_val"));
		return currentValue;
	}

	@Override
	public void increaseId(long byNumber) throws DatabaseException {
		Long currentValue = getCurrentId();
		Long nextValue = currentValue + byNumber;
		setId(currentValue, nextValue);
	}

}
