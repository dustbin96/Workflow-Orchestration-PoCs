package io.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.spring.model.PersonData;
import io.spring.model.SQLPerson;

public class PersonDataMapper implements RowMapper<PersonData> {

	@Override
	public PersonData mapRow(ResultSet rs, int rowNum) throws SQLException {
		PersonData personData = new PersonData();
		personData.setId(rs.getInt("p.id"));
		personData.setName(rs.getString("p.name"));;
		personData.setDateTime(rs.getTimestamp("p.random_time"));
		
		SQLPerson sqlPerson = new SQLPerson();
		sqlPerson.setId(rs.getInt("p.person_id"));
		sqlPerson.setName(rs.getString("pp.Name"));
		sqlPerson.setEmail(rs.getString("pp.Email"));
		sqlPerson.setNumber(rs.getInt("pp.Number"));
		
		personData.setPerson(sqlPerson);
		return personData;
	}

}
