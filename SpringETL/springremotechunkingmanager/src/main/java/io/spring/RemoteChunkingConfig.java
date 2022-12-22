package io.spring;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowStep;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.PollableChannel;

import io.spring.model.SQLPerson;

@Configuration
@EnableBatchIntegration
@EnableBatchProcessing
@EnableIntegration
public class RemoteChunkingConfig {

	@Autowired
	private RemoteChunkingManagerStepBuilderFactory managerStepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jbf;
	
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("tcp://localhost:61616");
		factory.setTrustAllPackages(true);
		return factory;
	}
	
	//Configure the outbound flow (requests going to workers)
	@Bean
	public DirectChannel requests() {
		return new DirectChannel();
	}
	
	@Bean
	public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows
				.from(requests())
				.handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
				.get();
	}
	
	//Configure inbound flow (replies coming from workers)
	@Bean
	public QueueChannel replies() {
		return new QueueChannel();
	}
	
	//Configure inbound flow (replies coming from workers)
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows
				.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("replies"))
				.channel(replies())
				.get();
	}
	
//	//Configure the ChunkMessageChannelItemWriter
//	@Bean
//	public ItemWriter<Integer> itemWriter(){
//		MessagingTemplate messagingTemplate = new MessagingTemplate();
//		messagingTemplate.setDefaultChannel(requests());
//		messagingTemplate.setReceiveTimeout(2000);
//		ChunkMessageChannelItemWriter<Integer> chunkMessageChannelItemWriter = new ChunkMessageChannelItemWriter<>();
//		chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate);
//		chunkMessageChannelItemWriter.setReplyChannel(replies());
//		return chunkMessageChannelItemWriter;
//		
//	}
	
	@Bean
	public JdbcCursorItemReader<SQLPerson> itemReader(DataSource dataSource){
		System.out.println("Item Reader Hit");
		return new JdbcCursorItemReaderBuilder<SQLPerson>()
				.name("itemReader")
				.dataSource(dataSource)
				.fetchSize(100)
				.sql("SELECT * FROM person")
				.beanRowMapper(SQLPerson.class)
				.build();
	}
	
	@Bean
	public TaskletStep managerStep(ItemReader<SQLPerson> itemReader) {
		return this.managerStepBuilderFactory
				.get("managerStep")
				.chunk(100)
				.reader(itemReader)
				.outputChannel(requests())
				.inputChannel(replies())
				.build();
	}
	
	@Bean
	public Job remoteChunkingJob(TaskletStep managerStep) {
		return jbf.get("job").incrementer(new RunIdIncrementer()).flow(managerStep).end().build();
	}
	
}
