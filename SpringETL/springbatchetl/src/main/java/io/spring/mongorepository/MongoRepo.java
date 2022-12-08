package io.spring.mongorepository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.spring.mongomodel.MongoPerson;

public interface MongoRepo extends MongoRepository<MongoPerson, Integer> {

}
