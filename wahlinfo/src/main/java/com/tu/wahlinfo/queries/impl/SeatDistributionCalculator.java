package com.tu.wahlinfo.queries.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SeatDistributionCalculator {

    public Map<String, Integer> calculateDistribution() {
	long numberOfSeats = 0L; // get via query: Queries.AvailableSeats
	List<String[]> votesPerParty = new ArrayList<String[]>(); // get via
								  // query:
								  // Queries.relevantVotingPapersPerParty

	Set<PartyRankValue> seats = new TreeSet<PartyRankValue>();

	double divisor = 1D;

	while (seats.size() < numberOfSeats) {
	    for (String[] entry : votesPerParty) {
		seats.add(new PartyRankValue(entry[0], Long.parseLong(entry[1]) / divisor));
		divisor += 2;
	    }
	}

	Map<String, Integer> seatsPerParty = new HashMap<String, Integer>();
	long counter = numberOfSeats;
	for (PartyRankValue p : seats) {
	    if (seatsPerParty.get(p.getPartyName()) == null) {
		seatsPerParty.put(p.getPartyName(), 1);
	    } else {
		seatsPerParty.put(p.getPartyName(), seatsPerParty.get(p.partyName) + 1);
	    }
	    counter--;
	    if (counter == 0) {
		break;
	    }
	}
	return seatsPerParty;

    }

    class PartyRankValue implements Comparable<PartyRankValue> {

	private String partyName;
	private double rankValue;

	public PartyRankValue(String partyName, double rankValue) {
	    super();
	    this.partyName = partyName;
	    this.rankValue = rankValue;
	}

	public String getPartyName() {
	    return partyName;
	}

	public double getRankValue() {
	    return rankValue;
	}

	@Override
	public int compareTo(PartyRankValue o) {
	    if (o.rankValue == this.rankValue) {
		return 0;
	    } else if (o.rankValue > this.rankValue) {
		return -1;
	    } else {
		return 1;
	    }
	}

    }

}
