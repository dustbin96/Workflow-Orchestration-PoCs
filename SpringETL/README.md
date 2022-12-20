# Spring-based ETL services and tools

To perform batch processing, orchestrating, ordering, and scheduling of operations using Java-based implementations.

## Table of Contents

  1. [Spring Batch](#spring-batch)
  2. [JobRunr](#jobrunr)

## Comparison of tools

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

## Spring Batch

- [Spring Batch Introduction](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/spring-batch-intro.html)
- [Spring Batch Concept](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/domain.html)

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

### Local DB Project Test Environment & Dependencies

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

### Embedded Project Test Environment & Dependencies

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
- Easier to setup and use as compared to Spring Batch
