delete
from	WIListMandateDistribution lmd
where	lmd.electionYear 	= :electionYear;

delete
from 	WIDirectMandateDistribution dmd
where 	dmd.electionYear	= :electionYear;