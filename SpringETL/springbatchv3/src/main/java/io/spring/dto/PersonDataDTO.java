package io.spring.dto;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Document(collection = "personData")
public class PersonDataDTO {

	@MongoId
	private ObjectId id;

	private int personId;

	private String name;

	private Date date;

	public PersonDataDTO() {
	}

	public PersonDataDTO(ObjectId id, int personId, String name, Date date) {
		this.id = id;
		this.personId = personId;
		this.name = name;
		this.date = date;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
