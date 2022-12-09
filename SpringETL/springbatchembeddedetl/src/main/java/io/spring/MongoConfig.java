package io.spring;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;

import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@Configuration
public class MongoConfig {
	
	public MongoTemplate mongoTemplate() throws IOException {
		
		
		
		EmbeddedMongoFactoryBean mongoFactoryBean = new EmbeddedMongoFactoryBean();
		mongoFactoryBean.setBindIp("localhost");
		MongoClient mongoClient =  (MongoClient) mongoFactoryBean.getObject();
		return new MongoTemplate(mongoClient, "spring_etl");
	}
	

}
