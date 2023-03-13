package com.example.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "person")
@Getter
@Setter
@ToString
public class MongoPerson {

	@MongoId
	private int id;
	@Field("MongoName")
	private String mongoName;
	@Field("MongoEmail")
	private String mongoEmail;
	@Field("MongoNumber")
	private String mongoNumber;

}
