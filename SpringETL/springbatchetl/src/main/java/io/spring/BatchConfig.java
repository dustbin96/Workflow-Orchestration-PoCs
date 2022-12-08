package io.spring;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import io.spring.mongomodel.MongoPerson;
import io.spring.sqlmodel.SQLPerson;
//import io.spring.mongorepository.MongoRepo;
//import io.spring.sqlrepo.SQLRepo;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

//	@Autowired
//	private MongoRepo mongoRepo;
//	
//	@Autowired
//	private SQLRepo sqlRepo;
//	
//	@Autowired
//	public JobBuilder jobBuilder;
//
//	@Autowired
//	public StepBuilder stepBuilder;

//	@Autowired
//	public DataSource dataSource;

	@Bean
	public JdbcCursorItemReader<SQLPerson> reader(@Autowired DataSource dataSource) throws SQLException {
		System.out.println("-----Reading-----");
//		DriverManagerDataSource managerDataSource = new DriverManagerDataSource();
//		managerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//		managerDataSource.setUrl("jdbc:mysql://localhost:3306/spring_etl");
//		managerDataSource.setUsername("root");
//		managerDataSource.setPassword("Eliasroad1!");
//		sqlRepo.findAll();
		JdbcCursorItemReaderBuilder<SQLPerson> builder = new JdbcCursorItemReaderBuilder<>();
		builder.name("personItemReader");
		builder.dataSource(dataSource);
		builder.fetchSize(100);
		builder.sql("SELECT * FROM person");
		builder.rowMapper(new PersonRowMapper());

//		return dataSource.;
		return new JdbcCursorItemReaderBuilder<SQLPerson>().name("personItemReader").dataSource(dataSource)
				.fetchSize(100).sql("SELECT * FROM person").rowMapper(new PersonRowMapper()).build();
	}

	@Bean
	public PersonItemProcessor processor() {
		System.out.println("-----Processing-----");
		return new PersonItemProcessor();
	}

	@Bean
	public MongoItemWriter<MongoPerson> writer(@Autowired MongoTemplate mongoTemplate) {
		System.out.println("-----Writing-----");
		return new MongoItemWriterBuilder<MongoPerson>().template(mongoTemplate).collection("person").build();
	}

//	@Bean
//	public Job sampleJob(JobRepository jobRepository, Step sampleStep) {
//		return new JobBuilder("sampleJob", jobRepository)
//				.incrementer(new RunIdIncrementer())
//				.start(sampleStep).build();
//	}
	@Bean
	public Step PersonStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
			JdbcCursorItemReader<SQLPerson> reader, MongoItemWriter<MongoPerson> writer) throws IOException {
		return new StepBuilder("step", jobRepository).<SQLPerson, MongoPerson>chunk(10, platformTransactionManager)
				.reader(reader).processor(processor()).writer(writer).build();
//		return sbf.get("step").<Person, Person>chunk(10).reader(reader).processor(processor()).writer(writer).build();
	}

	@Bean
	public Job PersonJob(JobCompletionNotificationListener listener, JobRepository jobRepository, Step personStep) {
		return new JobBuilder("job", jobRepository).incrementer(new RunIdIncrementer()).listener(listener)
				.start(personStep).build();
//		return jbf.get("job").incrementer(new RunIdIncrementer()).listener(listener).flow(PersonStep).end().build();
	}

//	@Bean
//	public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
//		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
//		jobLauncher.setJobRepository(jobRepository);
//		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
//		jobLauncher.afterPropertiesSet();
//		return jobLauncher;
//	}
}
