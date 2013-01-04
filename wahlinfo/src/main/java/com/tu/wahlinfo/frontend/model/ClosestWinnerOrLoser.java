package com.tu.wahlinfo.frontend.model;

public class ClosestWinnerOrLoser {

	Candidate candidate;
	Integer voteDiff;
        Integer rank;
	

	public ClosestWinnerOrLoser() {

	}

	public ClosestWinnerOrLoser(Candidate candidate, Integer voteDiff,
			Integer rank) {
		super();
		this.candidate = candidate;
		this.voteDiff = voteDiff;
                this.rank = rank;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	public Integer getVotesReceived() {
		return voteDiff;
	}

	public void setVotesReceived(Integer votesReceived) {
		this.voteDiff = votesReceived;
	}

        public Integer getVoteDiff() {
            return voteDiff;
        }

        public void setVoteDiff(Integer voteDiff) {
            this.voteDiff = voteDiff;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }
}
