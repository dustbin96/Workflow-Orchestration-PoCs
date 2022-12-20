package io.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Controller;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;
import io.spring.repo.MongoRepo;
import io.spring.repo.SQLRepo;

@Controller
public class RepoController {

	public SQLRepo sqlRepo;
	
	public MongoRepo mongoRepo;
	
	
	public List<SQLPerson> getAllSQLPerson() {
		
		List<SQLPerson> sqlPersonList = new ArrayList<SQLPerson>();
		sqlRepo.findAll().forEach(sqlPersonList::add);
		return sqlPersonList;
	}
	
	public List<MongoPerson> processPerson(List<SQLPerson> sqlPersonList) {
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
	
	
	

}
