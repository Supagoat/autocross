package q.autocross.results;

import java.text.DecimalFormat;

public class Run implements Comparable<Run> {
	private int runNum;
	private double rawTime = -1;
	private double paxTime; 
	private double paxPenalties = 0;
	private int penalties;
	private boolean finished;
	private Session session;
	public static final DecimalFormat timeFormat = new DecimalFormat("0.000");
	
	public static final int FAKE_RUN_PENALITY_INDICATOR = 8675309;
	
	public enum Session {
		Morning(""), Afternoon(".2");
		private String sessionResultAppend;
		Session(String sessionAppend) {
			this.sessionResultAppend = sessionAppend;
		}
		
		public String sessionResultAppend() {
			return this.sessionResultAppend;
		}
	
	};
	
	public Run() {
		setFinished(true);
	}
	
	public static Run noRun() {
		Run r = new Run();
		r.setFinished(false);
		r.setPenalties(FAKE_RUN_PENALITY_INDICATOR);
		return r;
	}
	
	public double getComparableTime() {
		if(!isFinished()) {
			return 99999999;
		}
		return getPaxTime()+(2*getPaxPenalties());
	}
	
	public int getRunNum() {
		return runNum;
	}
	public Run setRunNum(int runNum) {
		this.runNum = runNum;
		return this;
	}

	public double getRawTime() {
		return rawTime;
	}
	public Run setRawTime(double rawTime) {
		this.rawTime = rawTime;
		return this;
	}
	public double getPaxTime() {
		return paxTime;
	}
	public Run setPaxTime(double paxTime) {
		this.paxTime = paxTime;
		return this;
	}
	public int getPenalties() {
		return penalties;
	}
	public Run setPenalties(int penalties) {
		this.penalties = penalties;
		return this;
	}
	public boolean isFinished() {
		return finished;
	}
	public Run setFinished(boolean finished) {
		this.finished = finished;
		return this;
	}
	public Session getSession() {
		return session;
	}
	public Run setSession(Session session) {
		this.session = session;
		return this;
	}
	
	public double getPaxPenalties() {
		return paxPenalties;
	}

	public Run setPaxPenalties(double paxPenalties) {
		this.paxPenalties = paxPenalties;
		return this;
	}

	@Override
	public int compareTo(Run o) {
		return Double.compare(Double.valueOf(getComparableTime()), Double.valueOf(o.getComparableTime()));
	}

	@Override
	public String toString() {
		return getPaxTime()+"+"+getPenalties()+" "+isFinished();
	}

	public static DecimalFormat getTimeFormat() {
		return timeFormat;
	}
	
	
}