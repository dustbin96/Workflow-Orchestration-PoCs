# Spring-based ETL services and tools

## Spring Batch

To perform batch processing of data.

- [Spring Batch Introduction](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/spring-batch-intro.html)
- [Spring Batch Concept](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/domain.html)



### Comments

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

#### First Test Run

- Time taken for the batch job: ~850s (14mins)
  - Fetch size: 10
  - Chunk: 10

#### Second Test Run

- Time taken for the batch job: ~203s (3.3 mins)
  - Fetch size: 100
  - Chunk: 100

#### Third Test Run

- Time taken for the batch job: ~256s (4.2 mins)
  - Fetch size: 1000
  - Chunk: 100

#### Fourth Test Run

- Time taken for the batch job: ~146s (2.4 mins)
  - Fetch size: 100
  - Chunk: 1000

#### Fifth Test Run

- Time taken for the batch job: ~203s (3.3 mins)
  - Fetch size: 1000
  - Chunk: 1000

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
