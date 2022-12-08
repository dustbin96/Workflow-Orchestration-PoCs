package io.spring;

import org.springframework.batch.item.ItemProcessor;

import io.spring.mongomodel.MongoPerson;
import io.spring.sqlmodel.SQLPerson;

public class PersonItemProcessor implements ItemProcessor<SQLPerson, MongoPerson> {

	@Override
	public MongoPerson process(SQLPerson item) throws Exception {
		System.out.println("Processor @ " + item.getName());
		MongoPerson p = new MongoPerson();
		p.setId(item.getId());
		p.setName(item.getName().toUpperCase());
		p.setEmail(item.getEmail());
		p.setNumber(item.getNumber());
		return p;
	}

}
