package io.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "io.spring.mongorepository", mongoTemplateRef = "mongoTemplate")
public class MongoConfig {
//	
//	@Bean
////	@ConfigurationProperties(prefix = "spring.data.mongodb")
//	public MongoClient mongoClient() {
//		ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/spring_etl");
//		MongoClientSettings clientSettings = MongoClientSettings
//				.builder()
//				.applyConnectionString(connectionString)
//				.build();
//		return MongoClients.create(clientSettings);
//	}
	
	@Bean
	public MongoTemplate mongoTemplate() {
		ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/spring_etl");
		MongoClientSettings clientSettings = MongoClientSettings
				.builder()
				.applyConnectionString(connectionString)
				.build();
		return new MongoTemplate(MongoClients.create(clientSettings), "spring_etl");
	}
	
}
