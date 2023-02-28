package com.example;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.example.helper.CustomMongoItemReader;
import com.example.model.MongoPerson;


@Repository
public class BatchRepo {

	public MongoItemReader<MongoPerson> itemReader(MongoTemplate mongoTemplate){
		CustomMongoItemReader reader = new CustomMongoItemReader();
		reader.setTemplate(mongoTemplate);
		reader.setName("reader");
		reader.setTargetType(MongoPerson.class);
		reader.setCollection("person");
		reader.setMatch(Aggregation.match(new Criteria().where("_id").is(1)));
		reader.setLookupOperation(Aggregation.lookup("personData", "_id", "_id", "test"));
		
		return reader;
	}
	

	public MongoItemWriter<MongoPerson> itemWriter(MongoTemplate mongoTemplate){
		return new MongoItemWriterBuilder<MongoPerson>()
		.collection("personDataLookup")
		.template(mongoTemplate)
		.build();
	}
	
//	public JdbcCursorItemReader<PersonData> itemReader(DataSource dataSource) throws SQLException{
//		return new JdbcCursorItemReaderBuilder<PersonData>()
//				.name("reader")
//				.dataSource(dataSource)
//				.sql("SELECT * from persondata pd inner join person p where p.id = pd.person_id")
//				.rowMapper(new PersonDataMapper())
////				.beanRowMapper(PersonData.class)
//				.fetchSize(1000)
//				.build();
//	}
//	
//	public MongoItemWriter<PersonDataDTO> itemWriter(MongoTemplate mongoTemplate){
//		return new MongoItemWriterBuilder<PersonDataDTO>()
//				.collection("personData")
//				.template(mongoTemplate)
//				.build();
//	}
	
}
