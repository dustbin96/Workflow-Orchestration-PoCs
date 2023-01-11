package io.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import io.spring.dto.PersonDataDTO;
import io.spring.model.PersonData;
import io.spring.model.SQLPerson;

@Mapper
public interface PersonDataMapper {

	PersonDataMapper personDataMapper = Mappers.getMapper(PersonDataMapper.class);
	
	@Mapping(ignore = true, target = "id")
	@Mapping(source="person.id", target="personId")
	@Mapping(source="dateTime", target="date")
	PersonDataDTO personDataToPersonDataDTO(PersonData personData);
	
}
