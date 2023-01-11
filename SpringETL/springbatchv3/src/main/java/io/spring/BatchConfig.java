package io.spring;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import io.spring.dto.PersonDataDTO;
import io.spring.model.MongoPerson;
import io.spring.model.PersonData;
import io.spring.model.SQLPerson;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Bean
	public JdbcCursorItemReader<PersonData> itemReader(DataSource dataSource) throws SQLException{
		return new JdbcCursorItemReaderBuilder<PersonData>()
				.name("reader")
				.dataSource(dataSource)
				.sql("SELECT * from persondata p inner join person pp where pp.id = p.person_id ")
				.rowMapper(new PersonDataMapper())
//				.beanRowMapper(PersonData.class)
				.fetchSize(1000)
				.build();
	}
//	
	@Bean
	public PersonProcessor itemProcessor(){
		return new PersonProcessor();
	}
	
	@Bean
	public MongoItemWriter<PersonDataDTO> itemWriter(MongoTemplate mongoTemplate){
		return new MongoItemWriterBuilder<PersonDataDTO>()
				.collection("personData")
				.template(mongoTemplate)
				.build();
	}
	

	@Bean
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ItemReader<PersonData> itemReader, ItemWriter<PersonDataDTO> itemWriter) {
		return new StepBuilder("step", jobRepository)
				.allowStartIfComplete(true)
				.<PersonData, PersonDataDTO>chunk(1000, transactionManager)
				.reader(itemReader)
				.processor(itemProcessor())
				.writer(itemWriter)
				.build();
	}

	@Bean(name = "job")
	public Job job(JobRepository jobRepository, Step step, BatchListener listener) {
		return new JobBuilder("job", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(step)
				.listener(listener)
				.build();
	}
	
}
