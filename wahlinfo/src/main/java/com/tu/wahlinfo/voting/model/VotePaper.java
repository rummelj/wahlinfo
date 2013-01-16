package com.tu.wahlinfo.voting.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.Candidate;
import com.tu.wahlinfo.frontend.model.Party;

/**
 * http://www.leinfelden-echterdingen.de/servlet/PB/show/1416281/
 * Stimmzettel_262_2009.jpg
 * 
 * @author Johannes
 * 
 */
public class VotePaper {

	/**
	 * When is the election held? Should be current date in our case.
	 */
	Date electionDate;

	/**
	 * In which year was this vote paper issued?
	 */
	ElectionYear electionYear;

	/**
	 * Number of the electoral district.
	 */
	Integer electoralDistrictNumber;

	/**
	 * Printable name of the electoral District
	 */
	String electoralDistrictName;

	/**
	 * This is only valid if this list contains one candidate.
	 */
	List<Candidate> firstVote = new LinkedList<Candidate>();

	/**
	 * This is only valid if this list contains one party.
	 */
	List<Party> secondVote = new LinkedList<Party>();

	/**
	 * A collection of all candidates that can be voted in this electoral
	 * district with their list rank in the key.
	 */
	Map<Integer, Candidate> possibleFirstVotes = new HashMap<Integer, Candidate>();

	/**
	 * A collection of all parties that can be voted in this electoral district
	 * with their list rank in the key.
	 */
	Map<Integer, Party> possibleSecondVotes = new HashMap<Integer, Party>();

	/**
	 * A collection of all list candidates of a party. The list is assumed to be
	 * in the correct order.
	 */
	Map<Party, List<Candidate>> listCandidates = new HashMap<Party, List<Candidate>>();

	public VotePaper() {
		super();
	}

	public VotePaper(ElectionYear electionYear, Integer electoralDistrictNumber) {
		super();
		this.electionYear = electionYear;
		this.electoralDistrictNumber = electoralDistrictNumber;
	}

	public VotePaper(Date electionDate, ElectionYear electionYear,
			Integer electoralDistrictNumber, String electoralDistrictName,
			Map<Integer, Candidate> possibleFirstVotes,
			Map<Integer, Party> possibleSecondVotes,
			Map<Party, List<Candidate>> listCandidates) {
		super();
		this.electionDate = electionDate;
		this.electionYear = electionYear;
		this.electoralDistrictNumber = electoralDistrictNumber;
		this.electoralDistrictName = electoralDistrictName;
		this.possibleFirstVotes = possibleFirstVotes;
		this.possibleSecondVotes = possibleSecondVotes;
		this.listCandidates = listCandidates;
	}

	public VotePaper(Date electionDate, ElectionYear electionYear,
			Integer electoralDistrictNumber, String electoralDistrictName,
			List<Candidate> firstVote, List<Party> secondVote,
			Map<Integer, Candidate> possibleFirstVotes,
			Map<Integer, Party> possibleSecondVotes,
			Map<Party, List<Candidate>> listCandidates) {
		super();
		this.electionDate = electionDate;
		this.electionYear = electionYear;
		this.electoralDistrictNumber = electoralDistrictNumber;
		this.electoralDistrictName = electoralDistrictName;
		this.firstVote = firstVote;
		this.secondVote = secondVote;
		this.possibleFirstVotes = possibleFirstVotes;
		this.possibleSecondVotes = possibleSecondVotes;
		this.listCandidates = listCandidates;
	}

	public Date getElectionDate() {
		return electionDate;
	}

	public void setElectionDate(Date electionDate) {
		this.electionDate = electionDate;
	}

	public ElectionYear getElectionYear() {
		return electionYear;
	}

	public void setElectionYear(ElectionYear electionYear) {
		this.electionYear = electionYear;
	}

	public Integer getElectoralDistrictNumber() {
		return electoralDistrictNumber;
	}

	public void setElectoralDistrictNumber(Integer electoralDistrictNumber) {
		this.electoralDistrictNumber = electoralDistrictNumber;
	}

	public String getElectoralDistrictName() {
		return electoralDistrictName;
	}

	public void setElectoralDistrictName(String electoralDistrictName) {
		this.electoralDistrictName = electoralDistrictName;
	}

	public List<Candidate> getFirstVote() {
		return firstVote;
	}

	public void setFirstVote(List<Candidate> firstVote) {
		for (Candidate candidate : firstVote) {
			if (!possibleFirstVotes.containsValue(candidate)) {
				throw new IllegalArgumentException(
						"Cannot vote this candidate " + candidate);
			}
		}
		this.firstVote = firstVote;
	}

	public List<Party> getSecondVote() {
		return secondVote;
	}

	public void setSecondVote(List<Party> secondVote) {
		for (Party party : secondVote) {
			if (!possibleSecondVotes.containsValue(party)) {
				throw new IllegalArgumentException("Cannot vote this party "
						+ party);
			}
		}
		this.secondVote = secondVote;
	}

	public List<Candidate> getDirectCandidates() {
		List<Candidate> result = new ArrayList<Candidate>(
				getPossibleFirstVotes().size());
		for (Candidate candidate : getPossibleFirstVotes().values()) {
			result.add(candidate);
		}

		Collections.sort(result, new Comparator<Candidate>() {

			@Override
			public int compare(Candidate arg0, Candidate arg1) {
				return getRank(arg1) - getRank(arg0);
			}

			public int getRank(Candidate c) {
				for (Entry<Integer, Candidate> entry : getPossibleFirstVotes()
						.entrySet()) {
					if (entry.getValue().equals(c)) {
						return entry.getKey();
					}
				}
				return 0;
			}

		});

		return result;
	}

	public List<Candidate> getPartiesListCandidates(Party party) {
		return listCandidates.get(party);
	}

	public List<Party> getListParties() {
		List<Party> result = new ArrayList<Party>(getPossibleSecondVotes()
				.size());
		for (Party party : getPossibleSecondVotes().values()) {
			result.add(party);
		}

		Collections.sort(result, new Comparator<Party>() {

			@Override
			public int compare(Party arg0, Party arg1) {
				return getRank(arg1) - getRank(arg0);
			}

			public int getRank(Party c) {
				for (Entry<Integer, Party> entry : getPossibleSecondVotes()
						.entrySet()) {
					if (entry.getValue().equals(c)) {
						return entry.getKey();
					}
				}
				return 0;
			}

		});

		return result;
	}

	public Map<Integer, Candidate> getPossibleFirstVotes() {
		return possibleFirstVotes;
	}

	public void setPossibleFirstVotes(Map<Integer, Candidate> possibleFirstVotes) {
		this.possibleFirstVotes = possibleFirstVotes;
	}

	public Map<Integer, Party> getPossibleSecondVotes() {
		return possibleSecondVotes;
	}

	public void setPossibleSecondVotes(Map<Integer, Party> possibleSecondVotes) {
		this.possibleSecondVotes = possibleSecondVotes;
	}

	public Map<Party, List<Candidate>> getListCandidates() {
		return listCandidates;
	}

	public void setListCandidates(Map<Party, List<Candidate>> listCandidates) {
		this.listCandidates = listCandidates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((electionYear == null) ? 0 : electionYear.hashCode());
		result = prime
				* result
				+ ((electoralDistrictNumber == null) ? 0
						: electoralDistrictNumber.hashCode());
		result = prime * result
				+ ((firstVote == null) ? 0 : firstVote.hashCode());
		result = prime * result
				+ ((secondVote == null) ? 0 : secondVote.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VotePaper other = (VotePaper) obj;
		if (electionYear != other.electionYear)
			return false;
		if (electoralDistrictNumber == null) {
			if (other.electoralDistrictNumber != null)
				return false;
		} else if (!electoralDistrictNumber
				.equals(other.electoralDistrictNumber))
			return false;
		if (firstVote == null) {
			if (other.firstVote != null)
				return false;
		} else if (!firstVote.equals(other.firstVote))
			return false;
		if (secondVote == null) {
			if (other.secondVote != null)
				return false;
		} else if (!secondVote.equals(other.secondVote))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VotePaper [electionDate=" + electionDate + ", electionYear="
				+ electionYear + ", electoralDistrictNumber="
				+ electoralDistrictNumber + ", electoralDistrictName="
				+ electoralDistrictName + ", firstVote=" + firstVote
				+ ", secondVote=" + secondVote + ", possibleFirstVotes="
				+ possibleFirstVotes + ", possibleSecondVotes="
				+ possibleSecondVotes + ", listCandidates=" + listCandidates
				+ "]";
	}

}
