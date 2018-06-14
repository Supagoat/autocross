package q.autocross.db;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import q.autocross.engine.Person;

public interface AutocrossMapper {
	@Select("SELECT * FROM Person WHERE person_id = #{person_id}")
	@Results(value = {
	@Result(property="clubId", column="club_id", javaType=String.class),
	@Result(property="personId", column="person_id", javaType=String.class),
	@Result(property="firstName", column="first_name", javaType=String.class),
	@Result(property="lastName", column="last_name", javaType=String.class),
	@Result(property="primaryNumber", column="primary_number", javaType=String.class),
	@Result(property="emailHash", column="email_hash", javaType=String.class)
	})
	public Person getPerson(String person_id);
}