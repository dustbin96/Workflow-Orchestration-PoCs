package io.spring.model;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;

@Document(collection = "jobrunr_etl")
public class MongoPerson {

	@Id
	private int id;
	private String mongoName;
	private String mongoEmail;
	private String mongoNumber;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMongoName() {
		return mongoName;
	}

	public void setMongoName(String mongoName) {
		this.mongoName = mongoName;
	}

	public String getMongoEmail() {
		return mongoEmail;
	}

	public void setMongoEmail(String mongoEmail) {
		this.mongoEmail = mongoEmail;
	}

	public String getMongoNumber() {
		return mongoNumber;
	}

	public void setMongoNumber(String mongoNumber) {
		this.mongoNumber = mongoNumber;
	}
}
