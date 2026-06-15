package org.dm.conveyor.service;

import org.dm.conveyor.kafka.KafkaProducerService;
import org.dm.conveyor.model.JobEvent;
import org.dm.conveyor.model.JobResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class JobExecutionService {

    private final Logger logger = LoggerFactory.getLogger(JobExecutionService.class);

    @Value("${conveyor.wait.ms:0}")
    private long waitTimeInMs = 0;

    KafkaProducerService kafkaProducer;

    @Autowired
    public JobExecutionService(KafkaProducerService kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void executeJob(JobEvent jobEvent) {
        long startTime = System.currentTimeMillis();
        waitTimeInMs = getWaitTimeParameter(jobEvent);
        JobResultEvent resultEvent;
        if (waitTimeInMs > 0) {
            sleep(waitTimeInMs);
        }
        long runtimeMs = System.currentTimeMillis() - startTime;
        try {
            resultEvent = new JobResultEvent(JobResultEvent.StateEnum.OK, JobResultEvent.NAME_SLACKER, String.format("I finished this job in just %s ms, no problem", runtimeMs), jobEvent.getId());
        } catch (Exception e) {
            String message = String.format("Error while executing job %s", jobEvent.getId());
            logger.error(message, e);
            resultEvent = new JobResultEvent(JobResultEvent.StateEnum.ERROR, JobResultEvent.NAME_SLACKER, message, jobEvent.getId());
        }

        kafkaProducer.sendResultEvent(null, resultEvent);
        logger.info("Job execution finished, Status: {}, {}", resultEvent.getState(), resultEvent.getDetails());
    }

    private long getWaitTimeParameter(JobEvent jobEvent) {
        long waitTime = waitTimeInMs;
        String data = jobEvent.getData();
        if (data != null && data.startsWith("conveyor.wait.ms=")) {
            String parameterValue = data.substring("conveyor.wait.ms=".length());
            try {
                waitTime = Long.parseLong(parameterValue);
            } catch (NumberFormatException e) {
                logger.error("Invalid wait time format in job event data: {}", parameterValue);
            }
        }
        return waitTime;
    }

    private void sleep(long millis) {
        logger.info("Sleeping for {} ms...", millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.debug("Woke up after {} ms", millis);
    }


}
