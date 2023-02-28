package com.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class SpringBatchAggregationApplication {

	public static void main(String[] args) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
//		SpringApplication.run(SpringBatchAggregationApplication.class, args);

		SpringApplication app = new SpringApplication(SpringBatchAggregationApplication.class);
		ApplicationContext ctx = app.run(args);

		JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
		Job job = ctx.getBean("job", Job.class);
		JobExplorer jobExplorer = ctx.getBean(JobExplorer.class);
		JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
//				.addLong("param1", System.currentTimeMillis())
				.getNextJobParameters(job)
				.toJobParameters();

		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
	}

}
