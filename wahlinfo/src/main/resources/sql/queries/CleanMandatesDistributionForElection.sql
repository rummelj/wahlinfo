delete
from	WIListMandateDistribution lmd
where	lmd.electionId 	= :electionId;

delete
from 	WIDirectMandateDistribution dmd
where 	dmd.electionId	= :electionId;