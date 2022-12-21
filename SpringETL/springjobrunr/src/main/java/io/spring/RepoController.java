package io.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;
import io.spring.repo.MongoRepo;
import io.spring.repo.SQLRepo;

@Controller
public class RepoController {

//	public SQLRepo sqlRepo;
//	
//	public MongoRepo mongoRepo;

	// JPA method takes 4239 seconds (70 mins~)
	// JdbcTemplate method takes 32 seconds
	public List<SQLPerson> getAllSQLPerson(JdbcTemplate jdbcTemplate) {
		System.out.println("Getting all records from MySQL");

		String sqlString = "SELECT * FROM person";

		return jdbcTemplate.query(sqlString, new BeanPropertyRowMapper(SQLPerson.class));
//		List<SQLPerson> sqlPersonList = new ArrayList<SQLPerson>();

//		sqlPersonList = sqlRepo.findAll();
//				.forEach(sqlPersonList::add);
//		System.out.println(sqlRepo.count());

//		sqlRepo.findAll().forEach(System.out::println);
//		return sqlRepo.findAll();
	}

	public List<MongoPerson> processPerson(List<SQLPerson> sqlPersonList) {
		System.out.println("Processing data");
		List<MongoPerson> mongoPersonList = new ArrayList<MongoPerson>();
		for (SQLPerson p : sqlPersonList) {
			MongoPerson mp = new MongoPerson();
			mp.setId(p.getId());
			mp.setMongoName(p.getName().toUpperCase());
			mp.setMongoEmail(p.getEmail());
			mp.setMongoNumber(String.valueOf(p.getNumber()));
			System.out.println(p.getName());
			mongoPersonList.add(mp);
		}
		return mongoPersonList;
	}

	public void insertMongoPersonRecords(List<MongoPerson> mongoPersonList, MongoTemplate mongoTemplate) {
		System.out.println("Inserting data");
		
		Stream<MongoPerson> mongoPersonStream = mongoPersonList.stream();
		BackgroundJob.enqueue(mongoPersonStream, (mp) -> mongoTemplate.save(mp));
		
	}

}
