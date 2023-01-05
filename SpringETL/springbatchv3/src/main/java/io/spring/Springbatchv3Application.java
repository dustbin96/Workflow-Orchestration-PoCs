package io.spring;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Springbatchv3Application {

	public static void main(String[] args) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
//		SpringApplication.run(Springbatchv3Application.class, args);
		
		SpringApplication app = new SpringApplication(Springbatchv3Application.class);
		ConfigurableApplicationContext ctx = app.run(args);
		
		JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
		Job job = ctx.getBean("job", Job.class);
		JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
		
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		BatchStatus batchStatus = jobExecution.getStatus();
		
//		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
//		jobLauncher.run(new , null);
//		
//		JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(null, null, null)
	}

}
