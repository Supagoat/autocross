package q.autocross.results;

import org.apache.commons.cli.Options;

public class ResultsOptions extends Options {

	public ResultsOptions() {
		addOption("m", true, "Number of runs in the morning");
		addOption("a", true, "Number of runs in the afternoon");
		addOption("e", true, "Path to emails excel file xls");
		addOption("p", true, "Path to the Master Participants file tsv");
		addOption("r", true, "Path to the results file tsv");

	}
	
}
