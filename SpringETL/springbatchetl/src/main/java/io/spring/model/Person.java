package io.spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity //Hibernate makes a table out of this annotation
public class Person {

	@Id
	private int id;
	private String Name;
	private String Email;
	private int Number;

//	public Person(String name, String email, int number) {
//		Name = name;
//		Email = email;
//		Number = number;
//	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public int getNumber() {
		return Number;
	}

	public void setNumber(int number) {
		Number = number;
	}

}
