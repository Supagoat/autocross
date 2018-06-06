package q.autocross.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.flywaydb.core.Flyway;

public class Migrator {
	public static void main(String[] args) throws Exception {
		// Create the Flyway instance
		BasicConfigurator.configure();
		Flyway flyway = new Flyway();
		DBProps props = loadProps();
		// Point it to the database
		flyway.setDataSource(props.getUri(), props.getUsername(), props.getPassword());
		flyway.setLocations("filesystem:"+props.getMigrationsDir());
		//System.out.println(flyway.getBaselineVersion());
		 flyway.migrate();
	}

	public static DBProps loadProps() throws IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("db.properties")));
		return new DBProps().setUri(prop.getProperty("uri")).setUsername(prop.getProperty("username"))
				.setPassword(prop.getProperty("password")).setMigrationsDir(prop.getProperty("migrationsDir"));
	}

}