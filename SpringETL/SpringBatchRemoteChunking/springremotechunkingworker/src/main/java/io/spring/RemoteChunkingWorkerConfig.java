package io.spring;

import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import io.spring.model.MongoPerson;
import io.spring.model.SQLPerson;

@Configuration
@EnableBatchIntegration
@EnableBatchProcessing
@EnableIntegration
public class RemoteChunkingWorkerConfig {
	
	@Autowired
	private RemoteChunkingWorkerBuilder<SQLPerson, MongoPerson> workerBuilder;
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("tcp://localhost:61616");
		factory.setTrustAllPackages(true);
		return factory;
	}
	
	//Configure Inbound Flow (requests coming from Manager)
	@Bean
	public DirectChannel requests() {
		System.out.println("Requests from manager");
		return new DirectChannel();
	}
	
	@Bean
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		System.out.println("test");
		return IntegrationFlows
				.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("requests"))
				.log()
				.channel(requests())
				.get();
	}
	
	//Configure outbound flow (replies going to manager)
	@Bean
	public DirectChannel replies() {
		System.out.println("Replies to manager");
		return new DirectChannel();
	}
	
	@Bean
	public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) throws JMSException {
		return IntegrationFlows
				.from(replies())
				.log()
				.handle(Jms.outboundAdapter(connectionFactory).destination("replies"))
				.get();
	}
	
	@Bean
	public ItemProcessor<SQLPerson, MongoPerson> itemProcessor(){
		System.out.println("-----Processor Hit-----");
		return new PersonProcessor();
	}
	
	@Bean
	public MongoItemWriter<MongoPerson> itemWriter(MongoTemplate mongoTemplate){
		System.out.println("-----Writer Hit-----");
		return new MongoItemWriterBuilder<MongoPerson>()
				.template(mongoTemplate)
				.collection("person")
				.build();
	}

	@Bean
	public IntegrationFlow workerIntegrationFlow(ItemWriter<MongoPerson> itemWriter) {
		return this.workerBuilder
				.itemProcessor(itemProcessor())
				.itemWriter(itemWriter)
				.inputChannel(requests())
				.outputChannel(replies())
				.build();
	}
}
