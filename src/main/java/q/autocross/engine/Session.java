package q.autocross.engine;

public class Session {
	
	public enum Singleton {

	    INSTANCE;
		private Session instance = new Session();
		public Session get() {
			return instance;
		}
	}
	
	public String getClubId() {
		return "8f1cbdfa-e846-4ede-926d-549d29a34e01";
	}
	/*public Data getData(String session) {
		return 
	}
	*/
}