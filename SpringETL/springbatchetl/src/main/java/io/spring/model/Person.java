package io.spring.model;

import javax.persistence.Entity;
import javax.persistence.Id;

//`id` int(9) unsigned NOT NULL AUTO_INCREMENT,
//`Name` varchar(100) NOT NULL,
//`Email` varchar(100) NOT NULL,
//`Number` int(9) unsigned NOT NULL,

@Entity
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
