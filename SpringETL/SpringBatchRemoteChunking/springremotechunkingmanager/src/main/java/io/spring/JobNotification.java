package io.spring;

import org.bson.Document;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCursor;

@Component
public class JobNotification implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Before Job");

	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("!!!JOB FINISHED!!!");

			
			
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			System.out.println("!!!JOB FAILED!!!");
		}
		System.out.println("Start Time: " + jobExecution.getStartTime());
		System.out.println("End Time: " + jobExecution.getEndTime());
		
	}

}
