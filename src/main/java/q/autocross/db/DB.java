package q.autocross.db;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.gson.Gson;

import q.autocross.engine.Data;
import q.autocross.engine.InputProcessor;
import q.autocross.engine.Person;
import q.autocross.engine.Session;

public class DB {
	private AutocrossMapper mapper;
	private SqlSession session;
	
	public DB() {
		connect();
	}
	
	protected DB connect() {
		try {

			String resource = "d:\\dev\\autocross\\dbConfig.xml";
			Reader reader = new FileReader(new File(resource));
			SqlSessionFactory  sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			session = sqlSessionFactory.openSession();
			mapper = session.getMapper(AutocrossMapper.class);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return this;
	}
	
	public static void main(String[] args) {
		System.out.println("PERSON: "+new DB().connect().getPeopleById("person_id").getFirstName());
	}
	
	public Person getPeopleById(String id) {
		return mapper.getPerson(id);
	}
	
	public Person getPeopleByName(String firstName, String lastName) {
		return mapper.getPersonByName(firstName, lastName, Session.Singleton.INSTANCE.get().getClubId());
	}
	
	public Person getPeopleByEmail(String email) {
		return mapper.getPersonByEmail(InputProcessor.hashEmail(email), Session.Singleton.INSTANCE.get().getClubId());
	}
	
	public Data getData(String id) {
		//mapper.cleanSessionData();
		Gson gson = new Gson();
		return gson.fromJson(mapper.getData(id), Data.class);
	}
	
	public void insertData(String id, Data data) {
		mapper.deleteSessionData(id);
		Gson gson = new Gson();
		mapper.insertSessionData(id, gson.toJson(data));
		session.commit();
	}
	
}