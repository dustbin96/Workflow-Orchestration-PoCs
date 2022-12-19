package io.spring;

import org.springframework.batch.item.ItemProcessor;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;

public class PersonItemProcessor implements ItemProcessor<SQLPerson, MongoPerson> {

	//Processing of data to fit requirements
	@Override
	public MongoPerson process(SQLPerson item) throws Exception {
		System.out.println("Processor @ " + item.getName());
		MongoPerson p = new MongoPerson();
		p.setId(item.getId());
		p.setMongoName(item.getName().toUpperCase());
		p.setMongoEmail(item.getEmail());
		p.setMongoNumber(String.valueOf(item.getNumber()));
		return p;
	}

}
