package com.example.helper;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;

import com.example.model.MongoPerson;
import com.example.model.MongoPerson2;

public class MongoSecondItemProcessor implements ItemProcessor<MongoPerson, MongoPerson2> {
	private MongoTemplate mongoTemplate;
	
	public MongoSecondItemProcessor(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MongoPerson2 process(MongoPerson item) throws Exception {

		
		// Lookup existing items in "personDataLookup" collection compared with "person" collection that has the same ID as the previous item
		// If condition satisfy, all data in "personDataLookup" is inserted into 'test2' array in MongoPerson2
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(item.getId())),
				Aggregation.lookup("personDataLookup", "_id", "_id", "test2")
				);
		AggregationResults<MongoPerson2> aggResult = mongoTemplate.aggregate(agg, MongoPerson2.class, MongoPerson2.class);
		List<MongoPerson2> mp2List = aggResult.getMappedResults();
		System.out.println(mp2List.toString());

		
		// Check for records that satisfy the lookup condition
		if (!mp2List.isEmpty() && mp2List != null) {
			return mp2List.get(0);
		}
		
		return null;
	}

	
	
}
