package org.dm.conveyor.kafka;

import org.dm.conveyor.KafkaConfiguration;
import org.dm.conveyor.model.Job;
import org.dm.conveyor.model.JobEvent;
import org.dm.conveyor.model.JobResultEvent;
import org.dm.conveyor.service.JobExecutionService;
import org.dm.conveyor.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@KafkaListener(topics = {KafkaConfiguration.TOPIC_JOB, KafkaConfiguration.TOPIC_JOB_RESULT}, groupId = "check-file-group")
public class KafkaTopicListener {

    private final Logger logger = LoggerFactory.getLogger(KafkaTopicListener.class);

    private final JobService jobService;
    private final JobExecutionService jobExecutionService;

    private CountDownLatch countDownLatchJob = new CountDownLatch(1);
    private CountDownLatch countDownLatchResult = new CountDownLatch(1);

    @Autowired
    public KafkaTopicListener(JobService jobService, JobExecutionService jobExecutionService) {
        this.jobService = jobService;
        this.jobExecutionService = jobExecutionService;
    }

    @KafkaHandler
    public void handleJobMessage(JobEvent jobEvent) {
        logger.info("Job event is received: {}", jobEvent);
        jobService.updateState(jobEvent.getId(), Job.StateEnum.RUNNING);
        countDownLatchJob.countDown();
        logger.info("Starting job for: {}", jobEvent);
        jobExecutionService.executeJob(jobEvent);
    }

    @KafkaHandler
    public void handleJobResultMessage(JobResultEvent resultEvent) {
        logger.info("Result event is received: {}", resultEvent);
        jobService.addResult(resultEvent.getJobID(), resultEvent);
        jobService.updateState(resultEvent.getJobID(), Job.StateEnum.FINISHED);
        countDownLatchResult.countDown();
    }

    public void resetLatches() {
        countDownLatchJob = new CountDownLatch(1);
        countDownLatchResult = new CountDownLatch(1);
    }

    public CountDownLatch getCountDownLatchJob() {
        return countDownLatchJob;
    }

    public CountDownLatch getCountDownLatchResult() {
        return countDownLatchResult;
    }
}
