# Spring-based ETL services and tools

## Spring Batch

To perform batch processing of data.

- [Spring Batch Introduction](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/spring-batch-intro.html)
- [Spring Batch Concept](https://docs.spring.io/spring-batch/docs/4.0.x/reference/html/domain.html)

### Comments

- Facing lots of issues when trying to connect with dockerized databases (Not sure if its the work domain/connection blocking the ports)

  - Will be trying alternate methods (~~Reading/Writing of files~~ or In-memory databases &check;)

### Test Environment & Dependencies

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