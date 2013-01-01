package com.tu.wahlinfo.frontend.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.PieChartModel;

import com.tu.wahlinfo.analysis.IVoteAnalysis;
import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.Candidate;
import com.tu.wahlinfo.frontend.model.ClosestWinnerOrLoser;
import com.tu.wahlinfo.frontend.model.ElectoralDistrictWinner;
import com.tu.wahlinfo.frontend.model.OverhangMandate;
import com.tu.wahlinfo.frontend.model.Party;
import com.tu.wahlinfo.frontend.model.PartyAndSeats;
import com.tu.wahlinfo.persistence.DatabaseException;

@Named
@SessionScoped
public class AnalyseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5504368253639578387L;

	@Inject
	IVoteAnalysis voteAnalysis;

	static Map<String, Object> cache = new HashMap<String, Object>();

	public PieChartModel getSeatDistribution2005() throws DatabaseException,
			IOException {
		if (cache.containsKey("seatDistribution2005")) {
			return (PieChartModel) cache.get("seatDistribution2005");
		}
		cache.put("seatDistribution2005",
				getSeatDistribution(ElectionYear._2005));
		return getSeatDistribution2005();
	}

	public PieChartModel getSeatDistribution2009() throws DatabaseException,
			IOException {
		if (cache.containsKey("seatDistribution2009")) {
			return (PieChartModel) cache.get("seatDistribution2009");
		}
		cache.put("seatDistribution2009",
				getSeatDistribution(ElectionYear._2009));
		return getSeatDistribution2009();
	}

	PieChartModel getSeatDistribution(ElectionYear electionYear)
			throws DatabaseException, IOException {
		Map<Party, Integer> data = voteAnalysis
				.getSeatDistribution(electionYear);
		PieChartModel model = new PieChartModel();
		for (Entry<Party, Integer> dataEntry : data.entrySet()) {
			model.set(dataEntry.getKey().getName(), dataEntry.getValue());
		}
		return model;
	}

	@SuppressWarnings("unchecked")
	public List<PartyAndSeats> getSeatDistributionTable2005()
			throws DatabaseException, IOException {
		if (cache.containsKey("seatDistributionTable2005")) {
			return (List<PartyAndSeats>) cache.get("seatDistributionTable2005");
		}
		cache.put("seatDistributionTable2005",
				getSeatDistributionTable(ElectionYear._2005));
		return getSeatDistributionTable2005();
	}

	@SuppressWarnings("unchecked")
	public List<PartyAndSeats> getSeatDistributionTable2009()
			throws DatabaseException, IOException {
		if (cache.containsKey("seatDistributionTable2009")) {
			return (List<PartyAndSeats>) cache.get("seatDistributionTable2009");
		}
		cache.put("seatDistributionTable2009",
				getSeatDistributionTable(ElectionYear._2009));
		return getSeatDistributionTable2009();
	}

	List<PartyAndSeats> getSeatDistributionTable(ElectionYear electionYear)
			throws DatabaseException, IOException {
		Map<Party, Integer> data = voteAnalysis
				.getSeatDistribution(electionYear);
		List<PartyAndSeats> result = new LinkedList<PartyAndSeats>();
		for (Entry<Party, Integer> entry : data.entrySet()) {
			result.add(new PartyAndSeats(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Candidate> getMembersOfBundestag2005()
			throws DatabaseException, IOException {
		if (cache.containsKey("membersOfBundestag2005")) {
			return (List<Candidate>) cache.get("membersOfBundestag2005");
		}
		cache.put("membersOfBundestag2005",
				getMembersOfBundestag(ElectionYear._2005));
		return getMembersOfBundestag2005();
	}

	@SuppressWarnings("unchecked")
	public List<Candidate> getMembersOfBundestag2009()
			throws DatabaseException, IOException {
		if (cache.containsKey("membersOfBundestag2009")) {
			return (List<Candidate>) cache.get("membersOfBundestag2009");
		}
		cache.put("membersOfBundestag2009",
				getMembersOfBundestag(ElectionYear._2009));
		return getMembersOfBundestag2009();
	}

	List<Candidate> getMembersOfBundestag(ElectionYear electionYear)
			throws DatabaseException, IOException {
		List<Candidate> mandates = voteAnalysis.getDirMandates(electionYear);
		mandates.addAll(voteAnalysis.getListMandates(electionYear));
		return mandates;
	}

	public List<ElectoralDistrictWinner> getElectoralDistrictWinnersOverview2009() {
		return getElectoralDistrictWinnersOverview(ElectionYear._2009);
	}

	public List<ElectoralDistrictWinner> getElectoralDistrictWinnersOverview2005() {
		return getElectoralDistrictWinnersOverview(ElectionYear._2005);
	}

	List<ElectoralDistrictWinner> getElectoralDistrictWinnersOverview(
			ElectionYear electionYear) {
		List<ElectoralDistrictWinner> data = voteAnalysis
				.getElectoralDistrictWinnersOverview(electionYear);
		Collections.sort(data, new Comparator<ElectoralDistrictWinner>() {
			@Override
			public int compare(ElectoralDistrictWinner arg0,
					ElectoralDistrictWinner arg1) {
				return arg0.getElectoralDistrict().compareTo(
						arg1.getElectoralDistrict());
			}
		});
		return data;
	}

	public List<OverhangMandate> getOverhangMandates2009()
			throws DatabaseException, IOException {
		return getOverhangMandates(ElectionYear._2009);
	}

	public List<OverhangMandate> getOverhangMandates2005()
			throws DatabaseException, IOException {
		return getOverhangMandates(ElectionYear._2005);
	}

	private List<OverhangMandate> getOverhangMandates(ElectionYear electionYear)
			throws DatabaseException, IOException {
		return voteAnalysis.getOverhangMandates(electionYear);
	}

	public List<ClosestWinnerOrLoser> getClosestWinnerOrLoser2005() {
		return getClosestWinnerOrLoser(ElectionYear._2005);
	}

	public List<ClosestWinnerOrLoser> getClosestWinnerOrLoser2009() {
		return getClosestWinnerOrLoser(ElectionYear._2009);
	}

	private List<ClosestWinnerOrLoser> getClosestWinnerOrLoser(
			ElectionYear electionYear) {
		return voteAnalysis.getClosestWinnerOrLosers(electionYear);
	}
}
