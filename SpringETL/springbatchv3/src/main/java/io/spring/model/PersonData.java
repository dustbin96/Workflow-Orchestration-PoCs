package io.spring.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "persondata")
public class PersonData {
	@Id
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "random_time")
	private Timestamp dateTime;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "person_id")
	private SQLPerson person;

	public PersonData() {
	}

	public PersonData(int id, String name, Timestamp dateTime, SQLPerson person) {
		this.id = id;
		this.name = name;
		this.dateTime = dateTime;
		this.person = person;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public SQLPerson getPerson() {
		return person;
	}

	public void setPerson(SQLPerson person) {
		this.person = person;
	}
	
}
