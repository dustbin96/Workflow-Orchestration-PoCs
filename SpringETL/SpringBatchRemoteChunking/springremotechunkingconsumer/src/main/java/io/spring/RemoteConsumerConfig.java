package io.spring;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.PollableChannel;

@Configuration
@EnableBatchIntegration
@EnableBatchProcessing
@EnableIntegration
public class RemoteConsumerConfig {

	@Autowired
	private JobBuilderFactory jbf;
	
	@Autowired
	private RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory;
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("tcp://localhost:61616");
		factory.setTrustAllPackages(true);
		return factory;
	}
	
	//Configure Inbound Flow (replies coming from workers)
	@Bean
	public PollableChannel replies() {
		System.out.println("replies from workers");
		return new QueueChannel();
	}
	
	@Bean
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows
				.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("replies"))
				.log()
				.channel(replies())
				.get();
	}
	
	@Bean
	public TaskletStep managerStep() {
		return this.managerStepBuilderFactory
				.get("managerStep")
				.inputChannel(replies())
//				.outputChannel(requests())
				.build();
	}

	@Bean
	public Job remoteChunkingJob(TaskletStep managerStep) {
		return this.jbf
				.get("job")
				.incrementer(new RunIdIncrementer())
				.start(managerStep)
				.build();
//				.flow(managerStep)
//				.end()
//				.build();
	}
	
}
