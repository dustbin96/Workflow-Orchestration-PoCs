package io.spring;

import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.nosql.mongo.MongoDBStorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.jobrunr.storage.sql.mysql.MySqlStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

import io.spring.repo.MongoRepo;
import io.spring.repo.SQLRepo;

@Configuration
public class Config {
	
	@Bean
	public JobScheduler initJobRunr(DataSource dataSource, MongoClient mongoClient, JobActivator jobActivator) {
		return JobRunr.configure()
				.useJobActivator(jobActivator)
				.useStorageProvider(SqlStorageProviderFactory.using(dataSource))
				.useBackgroundJobServer()
				.useDashboard()
				.initialize().getJobScheduler();
		
	}
	
}
