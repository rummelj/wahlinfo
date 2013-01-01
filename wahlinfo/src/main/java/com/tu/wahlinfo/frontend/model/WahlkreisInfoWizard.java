package com.tu.wahlinfo.frontend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.model.DatabaseResult;
import com.tu.wahlinfo.persistence.DatabaseAccessor;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
@SessionScoped
public class WahlkreisInfoWizard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6155079900796882380L;

	private static final Logger LOG = LoggerFactory
			.getLogger(WahlkreisInfoWizard.class);

	private static final List<String> electionYears = new ArrayList<String>(2);
	static {
		electionYears.add("2005");
		electionYears.add("2009");
	}

	@Inject
	IVoteAnalysis voteAnalysis;

	@Inject
	DatabaseAccessor databaseAccessor;

	ElectionYear year;
	Map<String, Integer> districts;
	String district;

	public String getElectionYear() {
		if (year == null) {
			return "2005";
		}
		return year.toCleanString();
	}

	public void setElectionYear(String electionYear) {
		if (electionYear.equals("2009")) {
			year = ElectionYear._2009;
		} else {
			year = ElectionYear._2005;
		}
		LOG.info("Selected year now is {}", year);
	}

	public List<String> getElectionYears() {
		return electionYears;
	}

	public List<String> getDistricts() {
		if (districts == null) {
			updateDistricts();
		}
		List<String> result = new ArrayList<String>(districts.keySet());
		Collections.sort(result);
		return result;
	}

	public String getDistrict() {
		if (district == null) {
			return getDistricts().get(0);
		}
		return district;
	}

	public void setDistrict(String district) {
		if (!getDistricts().contains(district)) {
			throw new IllegalArgumentException(
					"Cannot select a district that is not a district");
		}
		LOG.info("Selected district now is {}", district);
		this.district = district;
	}

	void updateDistricts() {
		try {
			DatabaseResult queryResult = databaseAccessor.executeQuery(
					"select number, name from wielectoraldistrict", "number",
					"name");
			this.districts = new HashMap<String, Integer>();
			for (Map<String, String> queryResultRow : queryResult) {
				this.districts.put(queryResultRow.get("name"),
						Integer.valueOf(queryResultRow.get("number")));
			}
		} catch (DatabaseException e) {
			LOG.error("Could not retrieve electoral districts", e);
		}
	}

	public String getWahlbeteiligung(boolean detailAnalysis) {
		return voteAnalysis.getVoteParticipation(year, getSelectedNumber(),
				detailAnalysis) + "%";
	}

	public String getDirectCandidate(boolean detailAnalysis) {
		return voteAnalysis.getVotedDirectCandidate(year, getSelectedNumber(),
				detailAnalysis).getName();
	}

	public List<PartyDetailVote> getVoteDetails(boolean detailAnalysis) {
		return voteAnalysis.getVoteDetails(year, getSelectedNumber(),
				detailAnalysis);
	}

	Integer getSelectedNumber() {
		return districts.get(district);
	}

}
