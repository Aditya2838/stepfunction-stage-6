package com.example.batchdemo.listener;

import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobLoggingListener implements JobExecutionListener {

    private static final Logger log =
            LoggerFactory.getLogger(JobLoggingListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.info(
                "Job [{}] starting. Start Time={}",
                jobExecution.getJobInstance().getJobName(),
                LocalDateTime.now()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        LocalDateTime start = jobExecution.getStartTime();

        LocalDateTime end =
                jobExecution.getEndTime() != null
                        ? jobExecution.getEndTime()
                        : LocalDateTime.now();

        Duration duration =
                start != null
                        ? Duration.between(start, end)
                        : Duration.ZERO;

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info(
                    "Job [{}] COMPLETED. Duration={} ms",
                    jobExecution.getJobInstance().getJobName(),
                    duration.toMillis()
            );

            // Future:
            // Update DocumentDB status
            // Notify AWS Step Function
        } else {

            log.error(
                    "Job [{}] FAILED. Duration={} ms, Status={}",
                    jobExecution.getJobInstance().getJobName(),
                    duration.toMillis(),
                    jobExecution.getStatus()
            );

            // Future:
            // Update failure status
            // Send Step Function failure notification
        }

        log.info(
                "Job [{}] finished. Start={}, End={}, Status={}",
                jobExecution.getJobInstance().getJobName(),
                start,
                end,
                jobExecution.getStatus()
        );
    }
}