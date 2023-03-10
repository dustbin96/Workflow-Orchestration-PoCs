package com.example;

import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.helper.CustomMongoItemReader;
import com.example.helper.MongoFirstItemProcessor;
import com.example.helper.MongoSecondItemProcessor;
import com.example.model.MongoPerson;
import com.example.model.MongoPerson2;


@Repository
public class BatchRepo {

	public MongoItemReader<MongoPerson> customMongoItemReader(MongoTemplate mongoTemplate){
		CustomMongoItemReader reader = new CustomMongoItemReader();
		reader.setTemplate(mongoTemplate);
		reader.setName("customMongoItemReader");
		reader.setTargetType(MongoPerson.class);
		reader.setCollection("person");
		reader.setMatch(Aggregation.match(new Criteria().where("_id").is(1)));
		reader.setLookupOperation(Aggregation.lookup("personData", "_id", "_id", "test"));
		
		return reader;
	}
	

	public MongoItemWriter<MongoPerson> customMongoItemWriter(MongoTemplate mongoTemplate){
		return new MongoItemWriterBuilder<MongoPerson>()
		.collection("personDataLookup")
		.template(mongoTemplate)
		.build();
	}

	public MongoItemReader<MongoPerson> mongoItemReader(MongoTemplate mongoTemplate){

		HashMap<String,Direction> sortMap = new HashMap<>();
		sortMap.put("_id",Direction.ASC);
		
		Query query = new Query();
		query.fields().include("_id");

		MongoItemReader<MongoPerson> reader = new MongoItemReaderBuilder<MongoPerson>()
				.name("mongoItemReader")
				.template(mongoTemplate)
				.collection("person")
				.targetType(MongoPerson.class)
				.query(query)
				.sorts(sortMap)
				.build();
		
		return reader;
	}
	
	public MongoFirstItemProcessor mongoFirstItemProcessor(MongoTemplate mongoTemplate) {
		return new MongoFirstItemProcessor(mongoTemplate);
	}

	public MongoSecondItemProcessor mongoSecondItemProcessor(MongoTemplate mongoTemplate) {
		return new MongoSecondItemProcessor(mongoTemplate);
	}

	public MongoItemWriter<MongoPerson> mongoItemWriter(MongoTemplate mongoTemplate){
		return new MongoItemWriterBuilder<MongoPerson>()
		.collection("personDataLookup2")
		.template(mongoTemplate)
		.build();
	}
	
}
