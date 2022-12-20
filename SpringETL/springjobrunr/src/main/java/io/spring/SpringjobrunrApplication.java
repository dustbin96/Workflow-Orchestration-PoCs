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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;
import io.spring.repo.MongoRepo;
import io.spring.repo.SQLRepo;

@SpringBootApplication
public class SpringjobrunrApplication {
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SpringjobrunrApplication.class, args);
		
		SpringjobrunrApplication.test123();

	}
	
	public static void test123() throws InterruptedException {

//		BackgroundJob.enqueue(() -> System.out.println("Test"));
//		BackgroundJob.enqueue(() -> System.out.println("LETS GOOOOOOOOOOOOOOOOOOOOO"));
//		
		System.out.println("Test run");
		
		RepoController controller = new RepoController();
		List<SQLPerson> sqlPersonList = controller.getAllSQLPerson();
		List<MongoPerson> mongoPersonList = controller.processPerson(sqlPersonList);
		
		
		// keep dashboard running by blocking the main thread
		Thread.currentThread().join();
	}
	
	
	

}
