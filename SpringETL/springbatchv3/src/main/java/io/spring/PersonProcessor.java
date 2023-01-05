package io.spring;

import org.springframework.batch.item.ItemProcessor;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;

public class PersonProcessor implements ItemProcessor<SQLPerson, MongoPerson> {

	@Override
	public MongoPerson process(SQLPerson item) throws Exception {
		System.out.println(item.getName());
		MongoPerson mp = new MongoPerson();
		mp.setId(item.getId());
		mp.setMongoName(item.getName());
		mp.setMongoEmail(item.getEmail());
		mp.setMongoNumber(String.valueOf(item.getNumber()));
		return mp;
	}

}
