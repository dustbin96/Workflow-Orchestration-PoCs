package io.spring;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.spring.model.Person;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jbf;
	
	@Autowired
	public StepBuilderFactory sbf;
	
	@Bean
	public JdbcCursorItemReader<Person> reader(DataSource dataSource){
		
		System.out.println("-----Reading-----");
		return new JdbcCursorItemReaderBuilder<Person>()
				.name("personItemReader")
				.dataSource(dataSource)
				.fetchSize(100)
				.sql("SELECT * FROM person")
				.rowMapper(new PersonRowMapper())
				.build();
	}
	
	@Bean
	public PersonItemProcessor processor() {
		System.out.println("-----Processing-----");
		return new PersonItemProcessor();
	} 
	
	@Bean
	public MongoItemWriter<Person> writer(MongoTemplate mongoTemplate){
		System.out.println("-----Writing-----");
		return new MongoItemWriterBuilder<Person>()
				.template(mongoTemplate)
				.collection("Person")
				.build();
	}
	
	@Bean
	public Step PersonStep(JdbcCursorItemReader<Person> reader, MongoItemWriter<Person> writer) throws IOException {
		return sbf.get("step")
				.<Person, Person> chunk(10)
				.reader(reader)
				.processor(processor())
				.writer(writer)
				.build();
	}
	
	@Bean
	public Job PersonJob(JobCompletionNotificationListener listener, Step PersonStep) {
		return jbf.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(PersonStep)
				.end()
				.build();
	}
	
}
