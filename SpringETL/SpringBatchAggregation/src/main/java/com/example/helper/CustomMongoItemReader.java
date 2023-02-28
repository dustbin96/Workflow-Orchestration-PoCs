package com.example.helper;

import java.util.Iterator;

import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Query;

import com.example.model.MongoPerson;
import com.mongodb.BasicDBObject;
import com.mongodb.internal.operation.CountOperation;

public class CustomMongoItemReader extends MongoItemReader<MongoPerson> {

	private MongoOperations template;
	private String collection;
    private Class<? extends MongoPerson> type;

	private MatchOperation matchOperation;
	private LookupOperation lookupOperation;
	
	public CustomMongoItemReader() {
		super();
	}
	
    public void setMatch(MatchOperation matchOperation) {
        this.matchOperation = matchOperation;
    }
    

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setTemplate(MongoOperations template) {
        this.template = template;
    }
    
    public void setTargetType(Class<? extends MongoPerson> type) {
        this.type = type;
    }

    public void setLookupOperation(LookupOperation lookupOperation) {
        this.lookupOperation = lookupOperation;
    }
    
	@Override
	@SuppressWarnings("unchecked")
	protected Iterator<MongoPerson> doPageRead(){
		
		Pageable pageRequest = PageRequest.of(page, pageSize);
		
		Aggregation agg = Aggregation.newAggregation(
				matchOperation,
				lookupOperation,
				Aggregation.skip(pageRequest.getPageNumber() * pageRequest.getPageSize()), 
				Aggregation.limit(pageRequest.getPageSize()))
				.withOptions(Aggregation.newAggregationOptions().cursorBatchSize(1000).build());
		
		return (Iterator<MongoPerson>) template.aggregate(agg, collection, type).iterator();
		
	}
	
}
