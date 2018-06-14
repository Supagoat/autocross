package q.autocross.db;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import q.autocross.engine.Person;

public class DB {
	private AutocrossMapper mapper;
	
	public DB() {
		connect();
	}
	
	protected DB connect() {
		try {

			String resource = "d:\\dev\\autocross\\dbConfig.xml";
			Reader reader = new FileReader(new File(resource));
			SqlSessionFactory  sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			mapper = sqlSessionFactory.openSession().getMapper(AutocrossMapper.class);
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
	
}