package io.spring.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.spring.model.PersonData;
import io.spring.model.SQLPerson;

public class PersonDataMapper implements RowMapper<PersonData> {

	@Override
	public PersonData mapRow(ResultSet rs, int rowNum) throws SQLException {
		PersonData personData = new PersonData();
		personData.setId(rs.getInt("pd.id"));
		personData.setName(rs.getString("pd.name"));;
		personData.setDateTime(rs.getTimestamp("pd.random_time"));
		
		SQLPerson sqlPerson = new SQLPerson();
		sqlPerson.setId(rs.getInt("pd.person_id"));
		sqlPerson.setName(rs.getString("p.Name"));
		sqlPerson.setEmail(rs.getString("p.Email"));
		sqlPerson.setNumber(rs.getInt("p.Number"));
		
		personData.setPerson(sqlPerson);
		return personData;
	}

}
