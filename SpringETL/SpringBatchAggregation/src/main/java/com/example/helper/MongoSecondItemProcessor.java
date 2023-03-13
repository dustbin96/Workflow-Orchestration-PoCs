package com.example.helper;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.example.model.MongoPerson;
import com.example.model.MongoPerson2;

public class MongoSecondItemProcessor implements ItemProcessor<MongoPerson, MongoPerson2> {
	private MongoTemplate mongoTemplate;
	
	public MongoSecondItemProcessor(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MongoPerson2 process(MongoPerson item) throws Exception {
		System.out.println(item.toString());
		// Match existing items in "personDataLookup" collection with the previous processed data that has the same ID
		// If condition satisfy, map result into MongoPerson POJO
		// Taking quite long to process, one item takes about 5-10 seconds to process. So 1000 items could potentially be 2-3 hours.
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("_id").is(item.getId()))
				);
		
		AggregationResults<MongoPerson2> aggResult = mongoTemplate.aggregate(agg, "personDataLookup", MongoPerson2.class);
		List<MongoPerson2> mpList = aggResult.getMappedResults();
//		System.out.println(mpList.toString());

		// Expected output is 1 record
		if (!mpList.isEmpty() && mpList != null) {
			return mpList.get(0);
		}
		
		return null;
	}
}
