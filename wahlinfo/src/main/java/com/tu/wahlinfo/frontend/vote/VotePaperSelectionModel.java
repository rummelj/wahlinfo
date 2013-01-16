package com.tu.wahlinfo.frontend.vote;

import java.io.Serializable;

import javax.enterprise.inject.Model;

@Model
public class VotePaperSelectionModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8715220852936470403L;

	Integer electoralDistrict;

	public Integer getElectoralDistrict() {
		return electoralDistrict;
	}

	public void setElectoralDistrict(Integer electoralDistrict) {
		this.electoralDistrict = electoralDistrict;
	}

}
