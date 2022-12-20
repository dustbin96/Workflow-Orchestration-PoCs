package io.spring.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;

@Repository
public interface MongoRepo extends MongoRepository<MongoPerson, Integer> {
//	public List<MongoPerson> processPerson(List<SQLPerson> sqlPersonList);
}
