package io.spring.service;

import org.springframework.batch.item.ItemProcessor;

import io.spring.dto.PersonDataDTO;
import io.spring.mapper.PersonDataMapper;
import io.spring.model.MongoPerson;
import io.spring.model.PersonData;
import io.spring.model.SQLPerson;

public class PersonProcessor implements ItemProcessor<PersonData, PersonDataDTO> {

	@Override
	public PersonDataDTO process(PersonData item) throws Exception {
		System.out.println("PersonData = " + item.getName());
		
//		//Default Object Conversion method
//		PersonDataDTO personDataDTO = new PersonDataDTO();
//		personDataDTO.setName(item.getName());
//		personDataDTO.setDate(item.getDateTime());
//		personDataDTO.setPersonId(item.getPerson().getId());
		
		//MapStruct Method
		PersonDataDTO personDataDTO = PersonDataMapper.personDataMapper.personDataToPersonDataDTO(item);
		System.out.println("PersonDataDTO Person ID  = " + personDataDTO.getPersonId());
		return personDataDTO;
	}

}
