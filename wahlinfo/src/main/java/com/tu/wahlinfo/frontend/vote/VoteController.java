package com.tu.wahlinfo.frontend.vote;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.frontend.model.Candidate;
import com.tu.wahlinfo.frontend.model.Party;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.IVoteSubmission;
import com.tu.wahlinfo.voting.model.VotePaper;

@Named
@SessionScoped
public class VoteController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4825476611603350843L;

	private static final Logger LOG = LoggerFactory
			.getLogger(VoteController.class);

	@Inject
	IVoteSubmission voteSubmission;

	@Inject
	VoteModel voteModel;

	VotePaper votePaper;
	Integer electoralDistrict;

	public String selectVotePaperParameters() {
		return "vote?faces-redirect=true";
	}

	public String openVotePage() {
		init();
		return "votepaper";
	}

	void init() {
		voteModel.setTan("");
		setVotePaper(voteSubmission.generateVotePaper(ElectionYear._2009,
				getElectoralDistrict()));
	}

	public String vote() {
		String tanUsed = voteModel.getTan();
		if (voteModel.getFirstVote() != null
				&& !voteModel.getFirstVote().isEmpty()) {
			Integer firstVote = Integer.valueOf(voteModel.getFirstVote());
			LOG.info("Index first vote: {}", firstVote);
			getVotePaper().setFirstVote(
					Collections.singletonList(getVotePaper()
							.getDirectCandidates().get(firstVote)));
		} else {
			getVotePaper().setFirstVote(new ArrayList<Candidate>());
		}

		if (voteModel.getSecondVote() != null
				&& !voteModel.getSecondVote().isEmpty()) {
			Integer secondVote = Integer.valueOf(voteModel.getSecondVote());
			LOG.info("Index second vote: {}", secondVote);
			getVotePaper().setSecondVote(
					Collections.singletonList(getVotePaper().getListParties()
							.get(secondVote)));
		} else {
			getVotePaper().setSecondVote(new ArrayList<Party>());
		}

		voteModel.setTan("");
		try {
			LOG.info("Voting {} with tan {}", getVotePaper(), tanUsed);
			if (voteSubmission.vote(getVotePaper(), tanUsed)) {
				return "voteCounted?faces-redirect=true";
			} else {
				return "voteFailed?faces-redirect=true";
			}
		} catch (DatabaseException e) {
			return "voteCounted?faces-redirect=true";
		} finally {
			init();
		}
	}

	public String getElectoralDistrictName() {
		for (Entry<String, Integer> ed : voteSubmission.getElectoralDistricts(
				ElectionYear._2009).entrySet()) {
			if (ed.getValue().equals(getElectoralDistrict())) {
				return ed.getKey();
			}
		}
		return "(unknown)";
	}

	public String getToday() {
		return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
	}

	public VotePaper getVotePaper() {
		return votePaper;
	}

	public void setVotePaper(VotePaper votePaper) {
		this.votePaper = votePaper;
	}

	public Integer getElectoralDistrict() {
		return electoralDistrict;
	}

	public void setElectoralDistrict(Integer electoralDistrict) {
		this.electoralDistrict = electoralDistrict;
	}
}
