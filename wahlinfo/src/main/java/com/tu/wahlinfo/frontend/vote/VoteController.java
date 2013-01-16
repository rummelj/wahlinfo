package com.tu.wahlinfo.frontend.vote;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tu.wahlinfo.csv.entities.impl.ElectionYear;
import com.tu.wahlinfo.persistence.DatabaseException;
import com.tu.wahlinfo.voting.IVoteSubmission;

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
	VotePaperSelectionModel votePaperSelectionModel;

	@Inject
	VoteModel voteModel;

	public String selectVotePaperParameters() {
		return "vote";
	}

	public String openVotePage() {
		voteModel.setTan("");
		voteModel.setVotePaper(voteSubmission.generateVotePaper(
				ElectionYear._2009, votePaperSelectionModel.electoralDistrict));
		return "votepaper";
	}

	public String vote() {
		String tanUsed = voteModel.getTan();
		voteModel.setTan("");
		try {
			LOG.info("Voting {} with tan {}", voteModel.getVotePaper(), tanUsed);
			if (voteSubmission.vote(voteModel.getVotePaper(), tanUsed)) {
				return "voteCounted";
			} else {
				return "voteFailed";
			}
		} catch (DatabaseException e) {
			return "voteCounted";
		}
	}

	public String getElectoralDistrictName() {
		for (Entry<String, Integer> ed : voteSubmission.getElectoralDistricts(
				ElectionYear._2009).entrySet()) {
			if (ed.getValue().equals(
					votePaperSelectionModel.getElectoralDistrict())) {
				return ed.getKey();
			}
		}
		return "(unknown)";
	}

	public String getToday() {
		return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
	}
}
