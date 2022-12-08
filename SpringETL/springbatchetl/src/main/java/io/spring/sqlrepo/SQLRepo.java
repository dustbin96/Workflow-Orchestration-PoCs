package io.spring.sqlrepo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.spring.sqlmodel.SQLPerson;

public interface SQLRepo extends JpaRepository<SQLPerson, Integer> {

}
