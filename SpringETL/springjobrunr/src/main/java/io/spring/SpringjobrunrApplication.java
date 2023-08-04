package io.spring;

import java.util.List;

import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.nosql.mongo.MongoDBStorageProvider;
import org.jobrunr.storage.sql.common.DefaultSqlStorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.jobrunr.storage.sql.common.db.Sql;
import org.jobrunr.storage.sql.mysql.MySqlStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;
import io.spring.repo.MongoRepo;
import io.spring.repo.SQLRepo;

@EnableJpaRepositories(basePackageClasses = SQLRepo.class)
@EnableMongoRepositories(basePackageClasses = MongoRepo.class)
@SpringBootApplication
public class SpringjobrunrApplication implements CommandLineRunner {

	@Autowired
	public SQLRepo sqlRepo;
	
	@Autowired
	public MongoRepo mongoRepo;
	
	@Autowired
	public JdbcTemplate jdbcTemplate;
	
	@Autowired
	public MongoTemplate mongoTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringjobrunrApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {

//		BackgroundJob.enqueue(() -> System.out.println("Test"));
//		BackgroundJob.enqueue(() -> System.out.println("LETS GOOOOOOOOOOOOOOOOOOOOO"));
//		
		
		System.out.println("Test run");
		
		long startTime = System.currentTimeMillis();
		
		RepoController controller = new RepoController();
		List<SQLPerson> sqlPersonList = controller.getAllSQLPerson(jdbcTemplate);
		List<MongoPerson> mongoPersonList = controller.processPerson(sqlPersonList);
		controller.insertMongoPersonRecords(mongoPersonList, mongoTemplate);
		
		
		long endTime = System.currentTimeMillis();
		
//		while(mongoTemplate.count(new Query(), MongoPerson.class) != 1005228) {
//			
//		}

		BackgroundJob.enqueue(() -> controller.runWorkflow(jdbcTemplate, mongoTemplate));
		
		System.out.println("Took: " + ((endTime - startTime) / 1000) + " seconds");
		
		// keep dashboard running by blocking the main thread
		Thread.currentThread().join();
		
	}
	
	
	

}
