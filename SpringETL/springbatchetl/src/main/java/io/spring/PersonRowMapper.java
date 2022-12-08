package io.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.spring.sqlmodel.SQLPerson;

public class PersonRowMapper implements RowMapper<SQLPerson> {

	@Override
	public SQLPerson mapRow(ResultSet rs, int rowNum) throws SQLException {
		System.out.println(rs.getInt("id"));
		SQLPerson p = new SQLPerson();
		p.setId(rs.getInt("id"));
		p.setName(rs.getString("Name"));
		p.setEmail(rs.getString("Email"));
		p.setNumber(rs.getInt("Number"));
		return p;
	}

}
