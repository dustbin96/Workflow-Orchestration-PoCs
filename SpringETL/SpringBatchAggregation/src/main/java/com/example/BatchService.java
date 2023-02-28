package com.example;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.model.MongoPerson;

@Configuration
@EnableBatchProcessing
public class BatchService {

	private BatchRepo batchRepo;
	
	public BatchService(BatchRepo batchRepo) {
		this.batchRepo = batchRepo;
	}
	

	@Bean
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			MongoTemplate mongoTemplate) throws SQLException {
//			ItemReader<PersonData> itemReader, ItemWriter<PersonDataDTO> itemWriter
		return new StepBuilder("step", jobRepository)
				.allowStartIfComplete(true)
				.<MongoPerson, MongoPerson>chunk(1000, transactionManager)
				.reader(batchRepo.itemReader(mongoTemplate))
				.writer(batchRepo.itemWriter(mongoTemplate))
				.build();
	}

	@Bean
	public Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder("job", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(step)
//				.listener(listener)
				.build();
	}
	
}
