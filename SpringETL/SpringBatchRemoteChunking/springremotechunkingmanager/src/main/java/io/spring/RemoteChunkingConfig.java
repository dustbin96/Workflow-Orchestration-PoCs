package io.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowStep;
import org.springframework.batch.core.jsr.ChunkListenerAdapter;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ChunkListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.batch.integration.chunk.ChunkResponse;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.jms.JmsItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Ceil;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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
	private StepBuilderFactory sbf;
	
	@Autowired
	private JobBuilderFactory jbf;
	
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("tcp://localhost:61616");
		//Not a good security practice. Used for testing purposes only.
		factory.setTrustAllPackages(true);	
		return factory;
	}
	
	//Configure the outbound flow (requests going to workers)
	@Bean
	public QueueChannel requests() {
		return new QueueChannel();
	}
	
	@Bean
	public IntegrationFlow outboundFlow(ActiveMQConnectionFactory connectionFactory) {
		System.out.println("requests to workers");
		return IntegrationFlows
				.from(requests())
				.log()
				.handle(Jms.outboundAdapter(connectionFactory).destination("requests"))
				.get();
	}
	
	//Configure inbound flow (replies coming from workers)
	@Bean
	public PollableChannel replies() {
		return new QueueChannel();
	}
	
	//Configure inbound flow (replies coming from workers)
	public IntegrationFlow inboundFlow(ActiveMQConnectionFactory connectionFactory) {
		System.out.println("replies from workers");
		return IntegrationFlows
				.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination("replies"))
				.log()
				.channel(replies())
				.get();
	}
	
//	//Configure the ChunkMessageChannelItemWriter
//	@Bean
//	public ChunkMessageChannelItemWriter<Integer> itemWriter(){
//		MessagingTemplate messagingTemplate = new MessagingTemplate();
//		messagingTemplate.setDefaultChannel(requests());
//		messagingTemplate.setReceiveTimeout(1000);
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
				.fetchSize(1000)
				.sql("SELECT * FROM person")
				.beanRowMapper(SQLPerson.class)
				.build();
	}
	
//	@Bean
//	public ListItemReader<Integer> numberItemReader() {
//		return new ListItemReader<>(Arrays.asList(1, 2, 3, 4, 5, 6));
//	}
	
	// Get the total number of records in the database
	@Bean
	public int itemCount(DataSource dataSource) throws SQLException {
		Connection con = dataSource.getConnection();
		Statement statement = con.createStatement();
		ResultSet rs = statement.executeQuery("SELECT COUNT(1) AS total FROM person");
		rs.next();
		return rs.getInt("total");
		
	}
	
	// For some reason, the max pending messages will go up to 7-8 in the message queue
	// Will take 7 as the number to divide the SQL data into equal parts and round up
	// To consider using higher number of chunk size to ensure all data is written to the queue
	@Bean
	public TaskletStep managerStep(ItemReader<SQLPerson> itemReader, DataSource dataSource) throws SQLException {
		
		// To dynamically get "equal" splits of chunk size data to be passed into the queue
		float totalRecords = itemCount(dataSource);
		int chunkSize = (int) (Math.ceil((totalRecords / 7)/10000)*10000);
		System.out.println(chunkSize);
		
		return this.managerStepBuilderFactory
				.get("managerStep")
				.chunk(chunkSize)
				.reader(itemReader)
//				.readerIsTransactionalQueue()
				.inputChannel(replies())
				.outputChannel(requests())
				.build();
	}
	
	@Bean
	public Job remoteChunkingJob(TaskletStep managerStep, JobNotification listener) {
		return this.jbf
				.get("job")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.start(managerStep)
				.build();
//				.flow(managerStep)
//				.end()
//				.build();
	}
	
	
}
