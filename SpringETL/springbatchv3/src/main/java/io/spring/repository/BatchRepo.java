package io.spring.repository;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import io.spring.dto.PersonDataDTO;
import io.spring.model.PersonData;
import io.spring.service.PersonDataMapper;

// Repository layer to handle database operations
@Repository
public class BatchRepo {
	
//	@Bean
	public JdbcCursorItemReader<PersonData> itemReader(DataSource dataSource) throws SQLException{
		return new JdbcCursorItemReaderBuilder<PersonData>()
				.name("reader")
				.dataSource(dataSource)
				.sql("SELECT * from persondata pd inner join person p where p.id = pd.person_id")
				.rowMapper(new PersonDataMapper())
//				.beanRowMapper(PersonData.class)
				.fetchSize(1000)
				.build();
	}
	
//	@Bean
	public MongoItemWriter<PersonDataDTO> itemWriter(MongoTemplate mongoTemplate){
		return new MongoItemWriterBuilder<PersonDataDTO>()
				.collection("personData")
				.template(mongoTemplate)
				.build();
	}

}
