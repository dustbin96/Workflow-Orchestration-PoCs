# Areas of Exploration ordered by priority

1. **Aggregation**

    - How to implement Spring Batch's `CustomMongoItemReader` to work with Mongo Aggregation?
      - Extend `MongoItemReader` and provide own implementation for method `doPageRead()`?
        - [Example](#aggregation-custommongoitemreader)
      - Extend `CustomAggreagationPaginatedItemReader` and provide own implementation for method `doPageRead()`?
        - [Example](#aggregation-customaggreagationpaginateditemreader)
      - Implementing `skipOperation` for pagination?
      - What are the allowable aggregation operations that can be used with the custom reader e.g. `lookupOperation`?
        - <https://stackoverflow.com/questions/66684820/aggregation-from-mongodb-to-spring-boot-aggregation-framework>
    - Non-mongo aggregation way which uses Spring Batch's `Partitioning` which includes aggregation
![Paritioning Overview](/SpringETL/Media/partitioning-overview.png)
![Partitioning SPI](/SpringETL/Media/partitioning-spi.png)

2. 






## Code examples

### Aggregation-CustomMongoItemReader

```java
public class CustomMongoItemReader<T, O> extends MongoItemReader<T> {
private MongoTemplate template;
private Class<? extends T> inputType;
private Class<O> outputType
private MatchOperation match;
private ProjectionOperation projection;
private String collection;

@Override
protected Iterator<T> doPageRead() {
    Pageable page = PageRequest.of(page, pageSize) //page and page size are coming from the class that MongoItemReader extends
    Aggregation agg = newAggregation(match, projection, skip(page.getPageNumber() * page.getPageSize()), limit(page.getPageSize()));
    return (Iterator<T>) template.aggregate(agg, collection, outputType).iterator();
    }
}
```

### Aggregation-CustomAggreagationPaginatedItemReader

```java
public class CustomAggreagationPaginatedItemReader<T> extends AbstractPaginatedDataItemReader<T> implements InitializingBean {

    private MongoOperations template;
    private Class<? extends T> type;
    private Sort sort;
    private String collection;

    public CustomAggreagationPaginatedItemReader() {
        super();
    @Override
    @SuppressWarnings("unchecked")
    protected Iterator<T> doPageRead() {
        Pageable pageRequest = new PageRequest(page, pageSize, sort);

        BasicDBObject cursor = new BasicDBObject();
        cursor.append("batchSize", 100);

        SkipOperation skipOperation = skip(Long.valueOf(pageRequest.getPageNumber()) * Long.valueOf(pageRequest.getPageSize()));

        Aggregation aggregation = newAggregation(
                //Include here all your aggreationOperations,
                skipOperation,
                limit(pageRequest.getPageSize())
            ).withOptions(newAggregationOptions().cursor(cursor).build());

        return (Iterator<T>) template.aggregate(aggregation, collection, type).iterator();
    }
        return new Sort(sortValues);
    }
}
```


## Definition

### Mongo Aggregation

Aggregation operations process multiple documents and return computed results. You can use aggregation operations to:

- Group values from multiple documents together.
- Perform operations on the grouped data to return a single result.
- Analyze data changes over time.




