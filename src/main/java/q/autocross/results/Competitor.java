package q.autocross.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import q.autocross.results.Run.Session;

public class Competitor implements Comparable<Competitor>{

	private String firstName;
	private String lastName;
	private String number;
	private String bmwClass;
	private String pax;
	
	private List<Run> runs;
	
	public Competitor() {
		runs = new ArrayList<>();

	}
	
	public Run getBestRun(Run.Session session) {
		List<Run> sortedSession = getRuns().stream().filter(r -> r.getSession() == session && r.isFinished()).collect(Collectors.toList());
		Collections.sort(sortedSession);
		return sortedSession.size() == 0 ? Run.noRun() : sortedSession.get(0);
	}
	
	public double getTimeForTheDay() {
		return getBestRun(Session.Morning).getComparableTime()+getBestRun(Session.Afternoon).getComparableTime();
	}

	@Override
	public int compareTo(Competitor o) {
		Double totalTime = Double.valueOf(getTimeForTheDay());
		return totalTime.compareTo(Double.valueOf(o.getTimeForTheDay()));
	}
	
	
	public String getFirstName() {
		return firstName;
	}

	public Competitor setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public Competitor setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Competitor setNumber(String number) {
		this.number = number;
		return this;
	}


	public String getBmwClass() {
		return bmwClass;
	}

	public Competitor setBmwClass(String bmwClass) {
		this.bmwClass = bmwClass;
		return this;
	}

	public String getPax() {
		return pax;
	}

	public Competitor setPax(String pax) {
		this.pax = pax;
		return this;
	}

	public List<Run> getRuns() {
		return runs;
	}

	public Competitor setRuns(List<Run> runs) {
		this.runs = runs;
		return this;
	}

	@Override
	public int hashCode() {
		return getBmwClass().hashCode()+getPax().hashCode()+getNumber().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Competitor == false) {
			return false;
		}
		Competitor c = (Competitor)o;
		return this.getBmwClass().equals(c.getBmwClass()) && this.getPax().equals(c.getPax()) && this.getNumber().equals(c.getNumber()) && this.getRuns().equals(c.getRuns());
	} 
	
	@Override
	public String toString() {
		return getBmwClass()+" "+getPax()+" "+getNumber()+" "+getFirstName()+" "+getLastName();
	}
}