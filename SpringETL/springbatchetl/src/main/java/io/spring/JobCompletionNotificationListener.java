package io.spring;

import java.util.Arrays;

import org.bson.Document;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCursor;

//Custom listener component to initialize MongoTemplate and Metrics, 
//and to verify the job processing and completion
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private MongoTemplate mongoTemplate;
//	private DataSource dataSource;

	private MetricsEndpoint metrics;

	//Once job starts, automatically configure MongoTemplate and Metrics using the environment variables accordingly
	@Autowired
	public JobCompletionNotificationListener(MongoTemplate mongoTemplate, MetricsEndpoint metrics) {
		this.mongoTemplate = mongoTemplate;
//		this.dataSource = dataSource;
		this.metrics = metrics;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("------ Before Job ------");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("!!!JOB FINISHED!!!");

			MongoCursor<Document> cursor = mongoTemplate.getCollection("person").find().cursor();
			System.out.println(mongoTemplate.getCollection("person").countDocuments());
			cursor.forEachRemaining(System.out::println);

			double batchJobTimeTaken = metrics.metric("spring.batch.job", Arrays.asList()).getMeasurements().get(1)
					.getValue();
			System.out.println("Time taken for the batch job: " + batchJobTimeTaken);
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			System.out.println("!!!JOB FAILED!!!");
		}
	}
}
