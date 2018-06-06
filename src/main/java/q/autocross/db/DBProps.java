package q.autocross.db;

public class DBProps {
	private String uri;
	private String username;
	private String password;
	private String migrationsDir;

	public String getUri() {
		return uri;
	}
	public DBProps setUri(String uri) {
		this.uri = uri;
		return this;
	}
	public String getUsername() {
		return username;
	}
	public DBProps setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public DBProps setPassword(String password) {
		this.password = password;
		return this;
	}
	public String getMigrationsDir() {
		return migrationsDir;
	}
	public DBProps setMigrationsDir(String migrationsDir) {
		this.migrationsDir = migrationsDir;
		return this;
	}
	
	
	
	
}