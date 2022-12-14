# Spring-based ETL services and tools

To perform batch processing, orchestrating, ordering, and scheduling of operations using Java-based implementations.

## Table of Contents

  1. [Spring Batch](#spring-batch)
  2. [JobRunr](#jobrunr)

## General comparison of tools

||Spring Batch|JobRunr|
|---|---|---|
|Cost|Free|[Freemium](https://www.jobrunr.io/en/pricing/)|
|Background processing|[&check;](https://stackoverflow.com/questions/42511399/how-to-start-a-spring-batch-job-as-a-background-thread)|Inbuilt|
|Reccuring Jobs (Cron)|&check;|&check;|
|Parallel Processing|[&check;](https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#scalability)|Inbuilt|
|Job Chaining|&check;|Pro|
|Queue Priority|&cross;|Pro|
|Batch Processing|&check;|Pro|
|Metrics|&check;|&check;|
|Unit Test|[&check;](https://docs.spring.io/spring-batch/docs/current/reference/html/testing.html#testing)|?|
|Scalability by breaking down of tasks|[&check;](https://docs.spring.io/spring-batch/docs/current/reference/html/spring-batch-integration.html#externalizing-batch-process-execution)|&cross;|

## Spring Batch

- [Spring Batch Introduction](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/spring-batch-intro.html)
- [Spring Batch Concept](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/domain.html)
- [Simpler explanation of Spring Batch](https://medium.com/javarevisited/lets-learn-together-sessions-spring-batch-e690fd5428ec)

### Spring Batch Comments

- Facing issues when trying to connect with dockerized databases
  - Will be trying alternate methods for sake of time
    - ~~Reading/Writing of files~~
    - In-memory databases &check;
    - Local MySQL (Read) - MongoDB (Write) &check;
- Facing errors with Spring Boot v3 on creating JobBuilder/StepBuilder, downgrading to **v2.7.6** solves the error.
  - Errors when using JobBuilder/StepBuilder for MongoDB in Spring Boot v3
- When writing to MongoDB, a __class_ column is added by default(see below image). [Can be configured to be removed.](https://mkyong.com/mongodb/spring-data-mongodb-remove-_class-column/)

![MongoDB Class](/SpringETL/Media/MongoDBClass.png 'Default class property set by MongoDB')

- Spring Batch does not currently support MongoDB for JobRepository leading to lack of metadata and more complex setup for distributed transaction
  - Currently in works on adding into Spring Batch
    - <https://github.com/spring-projects/spring-batch/issues/877>
    - <https://stackoverflow.com/questions/66425674/spring-batch-with-mongodb-and-transactions>
- Can programatically configure most things e.g. Pojos, modifying of data, logging, fetch size, chunk, etc...
- Provides many other features e.g. Inbuilt metrics/actuator(Time taken for a job), parallel processing, remote chunking, and can be configured with Spring Cloud Data Flow(Orchestrator Tool)

### Experiment (Local MySQL to MongoDB)

  1. Read **1005228** records from MySQL and model it to a Pojo class
  2. Perform some processing if needed - In this example, one data type and field names were changed, and name field were made to be uppercase

<table>
<tr><th>SQLPerson model class</th><th>MongoPerson model class</th></tr>
<tr><td>

|id|Name|Email|Number|
|--|--|--|--|
|int|String|String|int|

</td><td>

|id|MongoName|MongoEmail|MongoNumber|
|--|--|--|--|
|int|String|String|String|

</td></tr> </table>

  3. Fit the retrieved/processed data to match with another separate Pojo class catered for MongoDB
  4. Write the data to MongoDB

![MySQL_MongoDB](/SpringETL/Media/MySQL_MongoDB_data.png 'MySQL to MongoDB migration')

#### **Test Runs**

||#1|#2|#3|#4|#5|
|---|---|---|---|---|---|
|**Fetch Size**|10|100|1000|100|1000|
|**Chunk**|10|100|100|1000|1000|
|**Time Taken(s)**|850|203|256|146|203|

### Spring Batch Project Setup

1. Get the project files from this repository
    - If creating a Spring Starter project from scratch using Spring Tool Suite, follow the [dependencies below](#local-spring-batch-db-project-test-environment--dependencies)
2. Ensure that MySQL and MongoDB is installed in your machine
3. Create a database/collection, named **spring_etl**, in MySQL and MongoDB
    - If using your custom database, change `spring.data.mongodb.database` and `spring.datasource.url` from **`application.properties`** in project to your corresponding database name
4. Mock data exists in [datatest.csv](/SpringETL/HelperData/datatest.csv) which can be used to add mock data in MySQL database
5. Check **`application.properties`** that the `spring.datasource.username` and `spring.datasource.password` is the same as your credentials
6. Once all is ready, run the application

### Local Spring Batch DB Project Test Environment & Dependencies

Project name - **springbatchetl**

- Local MySQL server database (v8)
  - Contains mock data of **1005228** records
- Local MongoDB server database (v6)
- Spring Boot Maven v2.7.6
  - Spring Web (To pair with using of actuator metrics)
  - Spring JPA (Relational data communication)
  - Spring JDBC (JDBC operations)
  - MongoDB (For MongoDB drivers)
  - MySQL Connector (MySQL Communication)
  - Spring Actuator to retrieve metrics e.g. Time taken for a batch job
  - Spring Batch for reading/writing batch data

## Spring Batch Remote Chunking

- [Spring Batch Remote Chunking Introduction](https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#remoteChunking)
- [Spring Batch Remote Chunking Details](https://docs.spring.io/spring-batch/docs/current/reference/html/spring-batch-integration.html#remote-chunking)

### Spring Batch Remote Chunking Comments

- Project consists of 1 producer and 2 workers with similar experiment as above
- A full run takes approximately **60s&plusmn;**

1. Producer application (_springremotechunkingmanager_) reads data from MySQL and sends data chunks of a POJO class to a queue facilitated by a Message Queue broker (using ActiveMQ in this project)
    - **Chunk size** is crucial in determining how much data to send to the queue and must ensure all data is written to the queue. If chunk size is too low, it may not send all the data due to the message queue limit
    - Data sent to the queue must either be an accepted type inbuilt with the Converter (e.g. SimpleMessageConverter) or a type that implements **Serializable** to serialize the data to be sent to the queue
2. Two separate worker applications (_springremotechunkingworker_ & _springremotechunkingworker2_) retrieve the data chunks from the queue, processes the data, and writes the data into a MongoDB. Results of each individual steps are then sent back to the Producer
    - Data received must either be one of the accepted types or a **Serializable** type to deserialize the incoming data
3. Producer receives the results and once all have been received, the job is done

### Spring Batch Remote Chunking Project Setup

1. Get the project files from this repository
2. Ensure that MySQL, MongoDB, and ActiveMQ 5 are installed in your machine
3. Create a database/collection, named **spring_etl**, in MySQL & MongoDB
4. Mock data exists in [datatest.csv](/SpringETL/HelperData/datatest.csv) which can be used to add mock data in MySQL database
5. Run ActiveMQ 5 Message Broker, here's a helpful guide: [Starting ActiveMQ Message Broker](https://activemq.apache.org/getting-started)
6. Run `mvnw clean install -DskipTests` on the producer and worker applications to generate their `.jar files`
7. Head into [ExecuteApps Folder](/SpringETL/SpringBatchRemoteChunking/ExecuteApps), copy the generated `.jar files` into it and execute the `run` .bat file to start the producer and worker applications

- For visibility of the ActiveMQ Queues, go to [ActiveMQ local dashboard](http://127.0.0.1:8161/admin/queues.jsp)

### Spring Batch Remote Chunking Project Test Environment & Dependencies

Project name - **SpringBatchRemoteChunking**

- Local MySQL server database (v8)
  - Contains mock data of **1005228** records
- Local MongoDB server database (v6)
- ActiveMQ 5 (Using as **broker**)
- Spring Boot Maven v2.7.*
  - Spring JPA (Relational data communication)
  - Spring JDBC (JDBC operations)
  - MongoDB (For MongoDB drivers)
  - MySQL Connector (MySQL Communication)
  - Spring Actuator
  - Spring Batch for reading/writing batch data
  - Spring ActiveMQ 5 (Only available for Spring Boot **v2.***)
  - Spring Integration
  - Spring Batch Integration (**Need to add this manually**)

### Spring Batch & Spring Boot v3

- [Need to manually configure and launch the job](https://stackoverflow.com/questions/22148117/spring-batch-error-a-job-instance-already-exists-and-runidincrementer-generate)

- Solutions
  - Follow the solutions above
  - An additional solution is adding `JobParameters,getNextJobParameters()`

- Metadata tables conflict with older version of spring boot and does not generate automatically into the database
  - [Spring Batch Metadata Schema tables](https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html#metaDataSchema)
  - [List of SQL schema creation and deletion](https://github.com/spring-projects/spring-batch/tree/main/spring-batch-core/src/main/resources/org/springframework/batch/core)
  - Currently using a `schema.sql` to create the metadata tables if the tables are not in the database

### Embedded DB Spring Batch Project Test Environment & Dependencies

Project name - **springbatchembeddedetl**

- Spring Boot Maven v2.7.6 (Embed Mongodb does not support v3)
  - H2 Database (In-memory SQL DB)
  - Embedded MongoDB (In-memory noSQL DB)
  - Spring Web (To pair with using of actuator metrics)
  - Spring JPA (Relational data communication)
  - MongoDB (For MongoDB drivers)
  - Flapdoodle's embedded mongodb
  - Jirutka's Spring Factory Bean for MongoDB for easier management
  - Spring Actuator to retrieve metrics e.g. Time taken for a batch job
  - Spring Batch for reading/writing batch data
- Schema.sql - For creation of table
- Data.sql - For mocking data

## JobRunr

- [JobRunr concept and architecture](https://www.jobrunr.io/en/documentation/)

### JobRunr Comments

- Since advanced features(e.g. Batch processing, job chaining, etc) are locked behind the Pro version, only a simple fire-and-forget job operation can be performed
  - Applicable only if simple tasks are expected to be performed
- Simpler to setup and operate as compared to Spring Batch
- Since jobs are all processed in the background, unable to retrieve results from these jobs (In free version)
- Good for executing individual small tasks in the background

### Local JobRunr DB Project Test Environment & Dependencies

Project name - springjobrunr

- Local MySQL server database (v8)
  - Contains mock data of **1005228** records
- Local MongoDB server database (v6)
- Spring Boot Maven v3
  - Spring Web (Comes with Json converter, Jackson)
  - Spring JPA (Relational data communication)
  - Spring JDBC (JDBC operations)
  - MongoDB (For MongoDB drivers)
  - MySQL Connector (MySQL Communication)
  - jobrunr-spring-boot-starter (Jobrunr with spring boot)

## Edge Cases

- Read/Write Locks
- What happens if read/write halfway from/to database and something interrupts, stopping the process
  - Rollback?
- If a worker app dies halfway, will the producer know and how to notify us?
