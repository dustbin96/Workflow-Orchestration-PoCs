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
- When writing to MongoDB, a __class_ column is added by default(see below image). [Can be configured to be removed.](https://mkyong.com/mongodb/spring-data-mongodb-remove-_class-column/)

[![MongoDB Class](/Media/MongoDBClass.png 'Default class property set by MongoDB')]

- Spring Batch does not currently support MongoDB for JobRepository leading to lack of metadata and more complex setup for distributed transaction
  - Currently in works on adding into Spring Batch
    - <https://github.com/spring-projects/spring-batch/issues/877>
    - <https://stackoverflow.com/questions/66425674/spring-batch-with-mongodb-and-transactions>

### Experiment

- 1005228 records
- Time taken for the batch job: 707.0619157
  - Fetch size: 256
  - Chunk: 10

### Embedded Project Test Environment & Dependencies

- Spring Boot v2.7.6 (Embed Mongodb does not support v3)
  - H2 Database (In-memory SQL DB)
  - Embedded MongoDB (In-memory noSQL DB)
  - Spring Web (To pair with using of actuator metrics)
  - Spring JPA (Relational data communication)
  - MongoDB (For MongoDB drivers)
  - Flapdoodle's embedded mongodb
  - Jirutka's Spring Factory Bean for MongoDB for easier management
  - Spring Actuator to retrieve metrics e.g. Time taken for a batch job
- Schema.sql - For creation of table
- Data.sql - For mocking data