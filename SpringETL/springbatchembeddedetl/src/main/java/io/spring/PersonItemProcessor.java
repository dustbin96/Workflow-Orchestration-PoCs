package io.spring;

import org.springframework.batch.item.ItemProcessor;

import io.spring.model.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	@Override
	public Person process(Person item) throws Exception {
//		System.out.println("Processor @ " + item.getName());
		Person p = new Person();
		p.setId(item.getId());
		p.setName(item.getName().toUpperCase());
		p.setEmail(item.getEmail());
		p.setNumber(item.getNumber());
		return p;
	}

}
