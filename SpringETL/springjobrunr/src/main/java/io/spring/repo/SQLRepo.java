package io.spring.repo;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.spring.model.SQLPerson;
import jakarta.persistence.QueryHint;

@Repository
public interface SQLRepo extends CrudRepository<SQLPerson, Integer> {

//	@QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value="1000"))
//	public List<SQLPerson> findAll();
	
}
