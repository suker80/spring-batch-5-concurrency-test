package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Runner {

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    Job sampleDataJob;

    @EventListener(ApplicationReadyEvent.class)
    public void runJob() throws JobExecutionException {
        System.out.println("러너 실행");
        JobParameters jobParameters = new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters();
        jobLauncher.run(sampleDataJob, jobParameters);
    }
}
