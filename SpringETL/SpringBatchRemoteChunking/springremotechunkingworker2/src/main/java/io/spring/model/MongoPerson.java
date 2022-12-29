package io.spring.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "person")
public class MongoPerson {

	@Id
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
