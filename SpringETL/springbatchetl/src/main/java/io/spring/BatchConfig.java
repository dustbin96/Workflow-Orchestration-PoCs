package io.spring;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import io.spring.model.Person;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jbf;

	@Autowired
	public StepBuilderFactory sbf;

	@Bean
	public JdbcCursorItemReader<Person> reader(DataSource dataSource) throws SQLException {
		System.out.println("-----Reading-----");
		return new JdbcCursorItemReaderBuilder<Person>().name("personItemReader").dataSource(dataSource).fetchSize(256)
				.sql("SELECT * FROM person").beanRowMapper(Person.class).build();
	}

	@Bean
	public PersonItemProcessor processor() {
		System.out.println("-----Processing-----");
		return new PersonItemProcessor();
	}

	@Bean
	public MongoItemWriter<Person> writer(MongoTemplate mongoTemplate) {
		System.out.println("-----Writing-----");
		return new MongoItemWriterBuilder<Person>().template(mongoTemplate).collection("person").build();
	}

	@Bean
	public Step PersonStep(JdbcCursorItemReader<Person> reader, MongoItemWriter<Person> writer) throws IOException {
		return sbf.get("step").<Person, Person>chunk(10).reader(reader).processor(processor()).writer(writer).build();
	}

	@Bean
	public Job PersonJob(JobCompletionNotificationListener listener, Step personStep) {
		return jbf.get("job").incrementer(new RunIdIncrementer()).listener(listener).flow(personStep).end().build();
	}
//	@Bean
//	public Step PersonStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
//			JdbcCursorItemReader<Person> reader, PersonItemProcessor processor, MongoItemWriter<Person> writer) throws IOException {
////		System.out.println(jobRepository.);
//		return new StepBuilder("step", jobRepository).<Person, Person>chunk(10, platformTransactionManager)
//				.reader(reader).processor(processor).writer(writer).build();
////		return sbf.get("step").<Person, Person>chunk(10).reader(reader).processor(processor()).writer(writer).build();
//	}
//
//	@Bean
//	public Job PersonJob(JobCompletionNotificationListener listener, JobRepository jobRepository, Step personStep) {
////		System.out.println(listener.);
//		return new JobBuilder("job", jobRepository).incrementer(new RunIdIncrementer()).listener(listener)
//				.start(personStep).build();
////		return jbf.get("job").incrementer(new RunIdIncrementer()).listener(listener).flow(PersonStep).end().build();
//	}
}
