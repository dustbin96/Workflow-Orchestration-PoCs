package com.example.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "person")
public class MongoPerson {

	@MongoId
	private int id;
	private String MongoName;
	private String MongoEmail;
	private String MongoNumber;
	
	private String[] test;

}
