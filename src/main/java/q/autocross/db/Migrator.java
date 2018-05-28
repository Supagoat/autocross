package q.autocross.db;

import org.flywaydb.core.Flyway;

public class Migrator {
	 public static void main(String[] args) throws Exception  {
	        // Create the Flyway instance
	        Flyway flyway = new Flyway();

	        // Point it to the database
	        flyway.setDataSource("jdbc:postgresql://bullet/autocross", "autocross", "autocross");
	        flyway.setLocations("classpath:db/autocross");

	        // Start the migration
	        flyway.migrate();
	    }
	
}