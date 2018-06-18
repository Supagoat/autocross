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
	public Person setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	public String getLastName() {
		return lastName;
	}
	public Person setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	public String getPrimaryNumber() {
		return primaryNumber;
	}
	public Person setPrimaryNumber(String primaryNumber) {
		this.primaryNumber = primaryNumber;
		return this;
	}
	
	public Person setUnhashedEmail(String email) {
		setEmailHash(Data.hashEmail(email));
		return this;
	}
	public String getEmailHash() {
		return emailHash;
	}
	public Person setEmailHash(String emailHash) {
		this.emailHash = emailHash;
		return this;
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