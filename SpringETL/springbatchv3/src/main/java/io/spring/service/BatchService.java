package io.spring.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import io.spring.dto.PersonDataDTO;
import io.spring.model.MongoPerson;
import io.spring.model.PersonData;
import io.spring.model.SQLPerson;
import io.spring.repository.BatchRepo;

@Configuration
@EnableBatchProcessing
public class BatchService {
	
	@Autowired
	private BatchRepo batchRepo;
	
	@Bean
	public PersonProcessor itemProcessor(){
		return new PersonProcessor();
	}

	@Bean
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			DataSource dataSource, MongoTemplate mongoTemplate) throws SQLException {
//			ItemReader<PersonData> itemReader, ItemWriter<PersonDataDTO> itemWriter
		return new StepBuilder("step", jobRepository)
				.allowStartIfComplete(true)
				.<PersonData, PersonDataDTO>chunk(1000, transactionManager)
				.reader(batchRepo.itemReader(dataSource))
				.processor(itemProcessor())
				.writer(batchRepo.itemWriter(mongoTemplate))
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
