package com.tu.wahlinfo.frontend.vote;

import java.io.Serializable;

import javax.enterprise.inject.Model;

@Model
public class VotePaperSelectionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8715220852936470403L;

	String electoralDistrict;

	public Integer getElectoralDistrictAsNumber() {
		try {
			return Integer.valueOf(electoralDistrict);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	public String getElectoralDistrict() {
		return electoralDistrict;
	}

	public void setElectoralDistrict(String electoralDistrict) {
		this.electoralDistrict = electoralDistrict;
	}

}
