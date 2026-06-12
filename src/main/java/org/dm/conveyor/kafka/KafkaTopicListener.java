package org.dm.conveyor.kafka;

import org.dm.conveyor.KafkaConfiguration;
import org.dm.conveyor.model.JobEvent;
import org.dm.conveyor.model.Job;
import org.dm.conveyor.model.JobResultEvent;
import org.dm.conveyor.service.JobService;
import org.dm.conveyor.service.JobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {KafkaConfiguration.TOPIC_CHECK_FILE,KafkaConfiguration.TOPIC_CHECK_RESULT}, groupId = "check-file-group")
public class KafkaTopicListener {

    private final Logger logger = LoggerFactory.getLogger(KafkaTopicListener.class);

    @Autowired
    JobService jobService;

    @Autowired
    JobExecutionService jobExecutionService;

    @KafkaHandler
    public void handleCheckMessage(JobEvent jobEvent) {
        logger.info("Check event is received: {}", jobEvent);
        jobService.updateState(jobEvent.getId(), Job.StateEnum.RUNNING);
        logger.info("Starting file check for: {}", jobEvent);
        jobExecutionService.executeJob(jobEvent);
    }

    @KafkaHandler
    public void handleResultMessage(JobResultEvent resultEvent) {
        logger.info("Result event is received: {}", resultEvent);
        jobService.addResult(resultEvent.getJobID(),resultEvent);
        jobService.updateState(resultEvent.getJobID(), Job.StateEnum.FINISHED);
    }
}
