package io.spring.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "person")
public class MongoPerson {

	@MongoId
	private int id;
	private String MongoName;
	private String MongoEmail;
	private String MongoNumber;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMongoName() {
		return MongoName;
	}

	public void setMongoName(String mongoName) {
		MongoName = mongoName;
	}

	public String getMongoEmail() {
		return MongoEmail;
	}

	public void setMongoEmail(String mongoEmail) {
		MongoEmail = mongoEmail;
	}

	public String getMongoNumber() {
		return MongoNumber;
	}

	public void setMongoNumber(String mongoNumber) {
		MongoNumber = mongoNumber;
	}

}
