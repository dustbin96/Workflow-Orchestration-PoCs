# Areas of Exploration ordered by priority

## 0. Aggregation

- How to implement Spring Batch's `CustomMongoItemReader` to work with Mongo Aggregation?
  - Extend `MongoItemReader` and provide own implementation for method `doPageRead()`?
    - [Code Example](#aggregation-custommongoitemreader)
  - Extend `CustomAggreagationPaginatedItemReader` and provide own implementation for method `doPageRead()`?
    - [Code Example](#aggregation-customaggreagationpaginateditemreader)
  - Implementing `skipOperation` for pagination?
  - What are the allowable aggregation operations that can be used with the custom reader e.g. `lookupOperation`?
    - <https://stackoverflow.com/questions/66684820/aggregation-from-mongodb-to-spring-boot-aggregation-framework>
  - Own local test with sample data and simple use case works with the customized `MongoItemReader`
    - Project Name: <https://github.com/dustbin96/Workflow-Orchestration-PoCs/tree/main/SpringETL/SpringBatchAggregation>

![Mongo Aggregation](/SpringETL/Media/SpringBatchMongoAggregation.png 'Mongo Aggregation')


## 1. Failure Handling and Rollbacks

- What are the different failure mechanisms?
  - By default, entire job terminates when an error occurs and logs the information into the Metadata schema depending on the error. An example below.
![Failed Metadata](/SpringETL/Media/SpringBatchFailedMetadata.PNG 'Failed Metadata')
  - Full status list applicable to step and job and whether it can be restarted
    - **Batch Status** - Indicates the status of the execution
      | Status    | Description                                                                       |
      |-----------|-----------------------------------------------------------------------------------|
      | ABANDONED | Status of a batch job that did not stop properly and can not be restarted.        |
      | COMPLETED | The batch job has successfully completed its execution.                           |
      | FAILED    | Status of a batch job that has failed during its execution. Can be restarted.     |
      | STARTED   | Status of a batch job that is running.                                            |
      | STARTING  | Status of a batch job prior to its execution.                                     |
      | STOPPED   | Status of a batch job that has been stopped by request. Can be restarted.         |
      | STOPPING  | Status of batch job waiting for a step to complete before stopping the batch job. |
      | UNKNOWN   | Status of a batch job that is in an uncertain state. Can not be restarted.        |
    - **Exit Status** - Indicates the result of the run. It is most important because it contains an exit code that will be returned to the caller. The field is empty if the job has yet to finish.
      | Status    | Description                                                                                                                        |
      |-----------|------------------------------------------------------------------------------------------------------------------------------------|
      | COMPLETED | Convenient constant value representing finished processing.                                                                        |
      | EXECUTING | Convenient constant value representing continuable state where processing is still taking place, so no further action is required. |
      | FAILED    | Convenient constant value representing finished processing with an error.                                                          |
      | NOOP      | Convenient constant value representing a job that did no processing (for example, because it was already complete).                |
      | STOPPED   | Convenient constant value representing finished processing with interrupted status.                                                |
      | UNKNOWN   | Convenient constant value representing unknown state - assumed to not be continuable.                                              |
    - [Batch Status v.s Exit Status](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#batchStatusVsExitStatus)
      - `Exit Status` message can be changed through either `JobExecutionListener` and/or `StepExecutionListener`

- What are the different default rollback behaviours?
  - `Chunk` model reads, process, writes, and commits based on the given `chunk size/commit interval`. If an error were to happen in the middle of the process, the current chunk of data processed will be rollbacked while the already processed chunks of data will be fully commited. Depending on the error faced, it can be restarted from where it left off based on the record count which can be found in the Job Repository: `batch_step_execution_context`
  
  ![Chunk Rollback](/SpringETL/Media/Ch05_transaction_TransactionControlChunkModel_rollback.png)
  
  - `Tasklet` model performs its operations repeatedly until it either returns `RepeatStatus.FINISHED` or throws an exception to signal a failure. It commits at the end once all is done.
  
  ![Tasklet Rollback](/SpringETL/Media/Ch05_transaction_TransactionControlTaskletModel_SingleTransaction_rollback.png)

- How can we configure rollover mechanism (what is the scope)?



- What are the important considerations that we need to take note of?
  - `skip` is a method of skipping error data without stopping batch processing and continuing processing. Explicitly request certain exceptions (and subclasses) to be skipped. Rollsback the current chunk that raised the error. 
    - [Code Example](#failure-handling-skip)
  - `noRollback` marks exceptions as ignorable during item read or processing operations. Processing continues with no additional callbacks (use skips instead if you need to be notified). Ignored during write because there is no guarantee of skip and retry without rollback. Continues with the chunk.
    - [Code Example](#failure-handling-norollback)
  - `Spring Retry` to be used with Spring Batch simplifies the execution of operations with retry semantics most frequently associated with handling transactional output exceptions. It is generally useful only when a subsequent invocation of the operation might succeed because something in the environment has improved such as network timeout. Explicitly ask for an exception (and subclasses) to be retried.
    - `FaultTolerantStepBuilder` contains  additional properties for retry and skip of failed items.
    - [Code Example](#failure-handling-retry-old)
  - `JobOperator` interface provides various operations on a job notably **restart** and **stop** operations which require the job's status to be restartable and its `executionId` to be passed in.
  - `skip` and `noRollback` seems to have a conflict and lack of clarification as per <https://github.com/spring-projects/spring-batch/issues/3748>

## 2. Chunk-based processing

- How is chunking being done? Is it deterministic?
  - Chunking is based on the assigned `chunk size/commit interval` which is a numeric value. Chunk oriented processing reads the data one at a time and create 'chunks' that are written out within a transaction boundary. Once the number of items read equals the commit interval, the entire chunk is written out by the `ItemWriter`, and then the transaction is committed
    - Chunk size should be increased to reduce overhead occurring due to resource output. However, if chunk size is too large, it increases load on the resources resulting in deterioration in the performance. Hence, chunk size must be adjusted to a moderate value
- Chunk model vs tasklet model [(Source)](https://terasoluna-batch.github.io/guideline/5.1.1.RELEASE/en/Ch03_ChunkOrTasklet.html)

![Chunk VS Tasklet](/SpringETL/Media/ChunkVSTasklet.PNG)

## 3. Stats

- What are the other fields we may need apart from what is already in the Job Metadata tables?
  - Unique parameters to define a job? E.g. job name, date, time, etc...
    - Helps to narrow down the job details in the Metadata schema
  - Built-in metrics which includes the duration of several tasks, and active jobs and steps. [(Source)](https://docs.spring.io/spring-batch/docs/current/reference/html/monitoring-and-metrics.html#built-in-metrics)

- How can we add in new ~~columns~~ of information?
  - `JobParameters` to uniquely identify every `JobInstance` [(Source)](https://docs.spring.io/spring-batch/docs/current/reference/html/domain.html#jobparameters)
    - Programatically, by adding new parameters using `JobParameters` object
    - Through the command line using `CommandLineJobRunner` [(Source)](https://docs.spring.io/spring-batch/docs/current/reference/html/job.html#commandLineJobRunner)
    - Job params added will be inserted into the Metadata schema `BATCH_JOB_EXECUTION_PARAMS` table but the "PARAMETER_VALUE" only accepts **VARCHAR(2500 char)**
  - Custom metrics can be created by using Micrometer APIs. [(Source)](https://docs.spring.io/spring-batch/docs/current/reference/html/monitoring-and-metrics.html#custom-metrics)

- How granular are these information?
  - The Metadata schema provided helps identify each job and step. `BATCH_STEP_EXECUTION` table has notable fields which has more granular information of the `StepExecution` such as `COMMIT_COUNT`, `READ_COUNT`, `WRITE_COUNT`, and more 

- Additonal information
  - Listeners which can provide additional information in specific methods ([Source](https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#interceptingStepExecution)):
    - JobExecutionListener
    - StepListener
    - ChunkListener
    - StepExecutionListener
    - ItemReadListener
    - ItemProcessListener
  - Link to DDL Scripts for the Metadata schema (<https://github.com/spring-projects/spring-batch/tree/main/spring-batch-core/src/main/resources/org/springframework/batch/core>)
  - Link to documentation of Metadata schema (<https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html#metaDataSchema>)

## 4. Parallelising
- Multi-threaded Step (single-process)
  - `Step` executes by reading, processing, and writing each chunk of items (each commit interval) in a separate thread of execution. **No fixed order** for the items to be processed, and a chunk might contain items that are **non-consecutive** compared to the single-threaded case.
  - Uses `TaskExecutor` implementation for asynchronous logic
  - Default throttle limit is 4 but can be increased to fully utilize the thread pool 
  - Limitations:
    - Many participants in a `Step` (such as readers and writers) are stateful. If the state is not segregated by thread, those components are not usable in a multi-threaded `Step`.
    - Most of the readers and writers from Spring Batch are not designed for multi-threaded use. It is, however, possible to work with stateless or thread safe readers and writers, that shows the use of a process indicator to keep track of items that have been processed in a database input table. If a reader is not thread safe, you can decorate it with the provided `SynchronizedItemStreamReader` or use it in your own synchronizing delegator. You can synchronize the call to `read()`, and, as long as the processing and writing is the most expensive part of the chunk, your step may still complete much more quickly than it would in a single-threaded configuration.
  - [Code Example](#parallelising---multi-threaded-steps-multi-threaded-steps)
- Parallel Steps / Split Flows (single-process)
  - Application logic needs to be parallelized and split into distinct responsibilities and assigned to individual steps, to be parallelized in a single process. No dependencies between jobs
  - Uses `TaskExecutor` implementation for asynchronous logic
  - Simpler and straightforward
  - [Code Example](#parallelising---parallel-stepsrallelising-)
- Remote Chunking
  - `Step` processing is split across **multiple processes**, communicating with each other through some **middleware** (e.g. a message queue)
  - Manager (single-process) and workers (multiple remote processes)
  - This pattern works best if the manager is not a bottleneck, so the processing must be more expensive than the reading of items (as is often the case in practice)
- Partitioning Steps
  - A `Step` instance in a `Job` is the manager which manages workers that are all identical instances of a `Step`
  - The messages sent by the manager to the workers in this pattern do not need to be durable or have guaranteed delivery
  - Spring Batch metadata in the `JobRepository` ensures that each worker is executed once and only once for each `Job` execution.

## 5. Configuration of Jobs (Step, Flow)
- Scope for configuration
  - Passing in data during **runtime** is possible using Late Binding by either using `-D` parameter as a system argument, or 

- Exception handling



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

### Failure Handling-Retry (OLD)

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

### Failure Handling-noRollback

Source - <https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#controllingRollback>

```java
@Bean
public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
	return new StepBuilder("step1", jobRepository)
				.<String, String>chunk(2, transactionManager)
				.reader(itemReader())
				.writer(itemWriter())
				.faultTolerant()
				.noRollback(ValidationException.class)
				.build();
}
```

### Parallelising - Multi-threaded Steps

Source - <https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#multithreadedStep>

```java
@Bean
public Step sampleStep(TaskExecutor taskExecutor, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
	return new StepBuilder("sampleStep", jobRepository)
				.<String, String>chunk(10, transactionManager)
				.reader(itemReader())
				.writer(itemWriter())
				.taskExecutor(taskExecutor)
				.throttleLimit(20)
				.build();
}
```

### Parallelising - Parallel Steps

Source - <https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#scalabilityParallelSteps>

Source - <https://docs.spring.io/spring-batch/docs/current/reference/html/step.html#split-flows>

```java
@Bean
public Job job(JobRepository jobRepository) {
    return new JobBuilder("job", jobRepository)
        .start(splitFlow())
        .next(step4())
        .build()        //builds FlowJobBuilder instance
        .build();       //builds Job instance
}

@Bean
public Flow splitFlow() {
    return new FlowBuilder<SimpleFlow>("splitFlow")
        .split(taskExecutor())
        .add(flow1(), flow2())
        .build();
}

@Bean
public Flow flow1() {
    return new FlowBuilder<SimpleFlow>("flow1")
        .start(step1())
        .next(step2())
        .build();
}

@Bean
public Flow flow2() {
    return new FlowBuilder<SimpleFlow>("flow2")
        .start(step3())
        .build();
}

@Bean
public TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor("spring_batch");
}


```
## Definition

### Mongo Aggregation

Aggregation operations process multiple documents and return computed results. You can use aggregation operations to:

- Group values from multiple documents together.
- Perform operations on the grouped data to return a single result.
- Analyze data changes over time.

### Spring Batch general process flow
![Process flow](/SpringETL/Media/Ch02_SpringBatchArchitecture_Architecture_ProcessFlow.png 'Process flow')

Source - <https://terasoluna-batch.github.io/guideline/5.1.1.RELEASE/en/Ch02_SpringBatchArchitecture.html#Ch02_SpringBatchArch>

## As of researched, Spring Batch Version 5.0.1
