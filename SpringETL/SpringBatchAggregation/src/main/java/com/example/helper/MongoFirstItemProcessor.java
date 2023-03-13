package com.example.helper;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import com.example.model.MongoPerson;

public class MongoFirstItemProcessor implements ItemProcessor<MongoPerson, MongoPerson> {

	private MongoTemplate mongoTemplate;
	
	public MongoFirstItemProcessor(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MongoPerson process(MongoPerson item) throws Exception {
		
		// Match existing items in "personData" collection with "person" collection based on several criteria
		// If condition satisfy, map result into MongoPerson POJO
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(item.getId()).and("MongoName").is("Culley")),
				Aggregation.lookup("personData", "_id", "_id", "test")
				);
		
		AggregationResults<MongoPerson> aggResult = mongoTemplate.aggregate(agg, MongoPerson.class, MongoPerson.class);
		
		List<MongoPerson> mpList = aggResult.getMappedResults();
		
		// Expected output is 1000 records
		if (!mpList.isEmpty() && mpList != null) {
			return mpList.get(0);
		}
		
		return null;
	}
}
