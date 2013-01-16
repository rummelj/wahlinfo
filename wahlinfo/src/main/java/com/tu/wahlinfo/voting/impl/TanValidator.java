package com.tu.wahlinfo.voting.impl;

import javax.ejb.Stateless;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.voting.ITanValidator;
import com.tu.wahlinfo.voting.model.VotePaper;

@Stateless
public class TanValidator implements ITanValidator {

	@Override
	public void validate(VotePaper votePaper, String tan)
			throws IllegalAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void invalidate(VotePaper votePaper, String tan) {
		// TODO Auto-generated method stub

	}

	@Override
	public void invalidateAll(ElectionYear year) {
		// TODO Auto-generated method stub

	}

}
