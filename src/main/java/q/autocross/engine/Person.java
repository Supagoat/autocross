package q.autocross.engine;

public class Person {
	private String clubId;
	private String personId;
	private String firstName;
	private String lastName;
	private String primaryNumber;
	private String emailHash;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPrimaryNumber() {
		return primaryNumber;
	}
	public void setPrimaryNumber(String primaryNumber) {
		this.primaryNumber = primaryNumber;
	}
	public String getEmailHash() {
		return emailHash;
	}
	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}
	public String getClubId() {
		return clubId;
	}
	public Person setClubId(String clubId) {
		this.clubId = clubId;
		return this;
	}
	public String getPersonId() {
		return personId;
	}
	public Person setPersonId(String personId) {
		this.personId = personId;
		return this;
	}
	
	
}