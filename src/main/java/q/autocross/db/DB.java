package q.autocross.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import q.autocross.engine.Person;

public class DB {
	private Connection connection;
	
	public DB() {
		connect();
	}
	
	protected void connect() {
		try {
		String url = "jdbc:postgresql://bullet/autocross";
		Properties props = new Properties();
		props.setProperty("user","autocross");
		props.setProperty("password","autocross");
		props.setProperty("ssl","false");
		connection = DriverManager.getConnection(url, props);
		} catch(Exception e) {
			e.printStackTrace();
		}
		//String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
		//Connection conn = DriverManager.getConnection(url);

	}
	
	public static void main(String[] args) {
		new DB();
	}
	
	/*public List<Person> getPeopleByNumber(String clubId, String number) {
		
	}
	*/
}