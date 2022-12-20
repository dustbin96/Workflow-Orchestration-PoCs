package io.spring.repo;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.spring.model.SQLPerson;

@Repository
public interface SQLRepo extends CrudRepository<SQLPerson, Integer> {

//	public List<SQLPerson> findAll();
	
}
