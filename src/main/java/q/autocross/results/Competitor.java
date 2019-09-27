package q.autocross.results;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import q.autocross.results.Run.Session;

public class Competitor implements Comparable<Competitor> {

	private String firstName;
	private String lastName;
	private String number;
	private String bmwClass;
	private String pax;
	private double paxModifier;
	private String novice;
	private String carModel;

	private List<Run> morningRuns;
	private List<Run> afternoonRuns;

	public Competitor() {
		morningRuns = new ArrayList<>();
		afternoonRuns = new ArrayList<>();
	}

	public Run getBestRun(Run.Session session) {
		List<Run> sortedSession = getRuns(session).stream().filter(r -> r.isFinished()).collect(Collectors.toList());
		Collections.sort(sortedSession);
		return sortedSession.size() == 0 ? Run.noRun() : sortedSession.get(0);
	}

	public double getTimeForTheDay() {
		return getBestRun(Session.Morning).getConedPaxedTime() + getBestRun(Session.Afternoon).getConedPaxedTime();
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

	public List<Run> getRuns(Run.Session type) {
		switch (type) {
		case Morning:
			return getMorningRuns();
		case Afternoon:
			return getAfternoonRuns();
		}
		return null;
	}

	public List<Run> getMorningRuns() {
		return morningRuns;
	}

	public Competitor setMorningRuns(List<Run> morningRuns) {
		this.morningRuns = morningRuns;
		return this;
	}

	public List<Run> getAfternoonRuns() {
		return afternoonRuns;
	}

	public Competitor setAfternoonRuns(List<Run> afternoonRuns) {
		this.afternoonRuns = afternoonRuns;
		return this;
	}

	public String getNovice() {
		return novice;
	}

	public Competitor setNovice(String novice) {
		this.novice = novice;
		return this;
	}
	

	public String getCarModel() {
		return carModel;
	}

	public Competitor setCarModel(String carModel) {
		this.carModel = carModel;
		return this;
	}

	public double getPaxModifier() {
		return paxModifier;
	}

	public Competitor setPaxModifier(double paxModifier) {
		this.paxModifier = paxModifier;
		return this;
	}

	public String getAvg(boolean raw) {
		if (!raw && (getPax() == null || getPax().isEmpty())) {
			return "-";
		}

		Run bestMorning = getBestRun(Session.Morning);
		Run bestAfternoon = getBestRun(Session.Afternoon);

		if (bestMorning == null || !bestMorning.isFinished() || bestAfternoon == null || !bestAfternoon.isFinished()) {
			return "-";
		}
		//TODO: This has gotten ugly looking, although it is correct.
		double totalTime = (raw? bestMorning.getRawTime() : bestMorning.getPaxTime())+(2*(raw? bestMorning.getPenalties() : bestMorning.getPaxPenalties()));
		totalTime += (raw? bestAfternoon.getRawTime() : bestAfternoon.getPaxTime())+(2*(raw? bestAfternoon.getPenalties() : bestAfternoon.getPaxPenalties()));
		return Run.getTimeFormat().format(totalTime / 2);
	}

	@Override
	public int hashCode() {
		return getBmwClass().hashCode() + getPax().hashCode() + getNumber().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Competitor == false) {
			return false;
		}
		Competitor c = (Competitor) o;
		return this.getBmwClass().equals(c.getBmwClass()) && this.getPax().equals(c.getPax())
				&& this.getNumber().equals(c.getNumber()) && this.getMorningRuns().equals(c.getMorningRuns())
				&& this.getAfternoonRuns().equals(c.getAfternoonRuns());
	}

	@Override
	public String toString() {
		return getBmwClass() + " " + getPax() + " " + getNumber() + " " + getFirstName() + " " + getLastName();
	}
}