# Areas of Exploration ordered by priority

## 0. Aggregation

- How to implement Spring Batch's `CustomMongoItemReader` to work with Mongo Aggregation?
  - Extend `MongoItemReader` and provide own implementation for method `doPageRead()`?
    - [Example](#aggregation-custommongoitemreader)
  - Extend `CustomAggreagationPaginatedItemReader` and provide own implementation for method `doPageRead()`?
    - [Example](#aggregation-customaggreagationpaginateditemreader)
  - Implementing `skipOperation` for pagination?
  - What are the allowable aggregation operations that can be used with the custom reader e.g. `lookupOperation`?
    - <https://stackoverflow.com/questions/66684820/aggregation-from-mongodb-to-spring-boot-aggregation-framework>
  - Own local test with sample data and simple use case works with the customized `MongoItemReader`
    - Project Name: <https://github.com/dustbin96/Workflow-Orchestration-PoCs/tree/main/SpringETL/SpringBatchAggregation>

## 1. Failure Handling and Rollbacks

- What are the different failure mechanisms?
  - By default, entire job terminates when an error occurs and JobRepository will log the information depending on the error
  - 

- What are the different default rollback behaviours?
- How can we configure rollover mechanism (what is the scope)?
- What are the important considerations that we need to take note of?
  - `skip` logic to skip corrupted data by skipping specified thrown exceptions to allow the step to continue running
    - [Example](#failure-handling-skip)
  - `retry` logic to retry the step operation which can be useful for cases such as network timeout
    - [Example](#failure-handling-retry)



https://code.likeagirl.io/the-ultimate-beginners-guide-for-spring-batch-error-handling-175aa258ecfa

## 3. Stats

- What are the other fields we may need apart from what is already in the Job Metadata tables?
  - Unique parameters to define a job? E.g. job name, date, time, etc...
    - Helps to narrow down the job details in the Job Metadata table

- How can we add in new columns of information?
- How granular are these information?

- Additonal information
  - Listeners which can provide additional information in specific methods:
    - JobExecutionListener
    - StepListener
    - ChunkListener
    - StepExecutionListener
    - ItemReadListener
    - ItemProcessListener

## Code examples

### Aggregation-CustomMongoItemReader

Source - <https://stackoverflow.com/a/52569974>

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

Source - <https://stackoverflow.com/a/57658116>

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

### Failure Handling-Skip

Source - <https://www.baeldung.com/spring-batch-skip-logic>

```java
@Bean
public Step skippingStep(
  ItemProcessor<Transaction, Transaction> processor,
  ItemWriter<Transaction> writer) throws ParseException {
    return stepBuilderFactory
      .get("skippingStep")
      .<Transaction, Transaction>chunk(10)
      .reader(itemReader(invalidInputCsv))
      .processor(processor)
      .writer(writer)
      .faultTolerant()
      .skipLimit(2)
      .skip(Exception.class)
      .noSkip(SAXException.class)
      .build();
}
```

### Failure Handling-Retry

Source - <https://www.baeldung.com/spring-batch-retry-logic>

```java
@Bean
public Step retryStep(
  ItemProcessor<Transaction, Transaction> processor,
  ItemWriter<Transaction> writer) throws ParseException {
    return stepBuilderFactory
      .get("retryStep")
      .<Transaction, Transaction>chunk(10)
      .reader(itemReader(inputCsv))
      .processor(processor)
      .writer(writer)
      .faultTolerant()
      .retryLimit(3)
      .retry(ConnectTimeoutException.class)
      .retry(DeadlockLoserDataAccessException.class)
      .build();
}
```


## Definition

### Mongo Aggregation

Aggregation operations process multiple documents and return computed results. You can use aggregation operations to:

- Group values from multiple documents together.
- Perform operations on the grouped data to return a single result.
- Analyze data changes over time.




