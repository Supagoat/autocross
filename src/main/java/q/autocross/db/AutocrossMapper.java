package q.autocross.db;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import q.autocross.engine.Person;

public interface AutocrossMapper {
	@Select("SELECT * FROM Person WHERE person_id = #{person_id}")
	@Results(id = "person", value = {
	@Result(property="clubId", column="club_id", javaType=String.class),
	@Result(property="personId", column="person_id", javaType=String.class),
	@Result(property="firstName", column="first_name", javaType=String.class),
	@Result(property="lastName", column="last_name", javaType=String.class),
	@Result(property="primaryNumber", column="primary_number", javaType=String.class),
	@Result(property="emailHash", column="email_hash", javaType=String.class)
	})
	public Person getPerson(String person_id);
	
	@Select("SELECT * FROM Person WHERE person_id = #{person_id} and club_id = #{clubId}")
	@ResultMap("person")
	public Person getPersonByEmail(String emailHash, String clubId);
	
	@Select("select * from Person where first_name= #{firstName} and last_name=#{lastName} and club_id = #{clubId}")
	@ResultMap("person")
	public Person getPersonByName(String firstName, String lastName, String clubId);
	
	@Delete("delete from session_data where updated &lt; now()- interval '12 hour'")
	public void cleanSessionData();
	
	@Delete("delete from session_data where id = #{id}")
	public void deleteSessionData(@Param("id") String id);
	
	@Select("select data from session where id = #{id}")
	@Results(id = "data", value = {
			@Result(property="data", column="data", javaType=String.class),
	})
	public String getData(String id);
	
	@Insert("insert into session_data (id, data) values (#{id}, #{data})")
	public void insertSessionData(@Param("id") String id, @Param("data")String data);
}