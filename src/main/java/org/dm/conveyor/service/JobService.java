package org.dm.conveyor.service;

import org.dm.conveyor.kafka.KafkaProducerService;
import org.dm.conveyor.model.Job;
import org.dm.conveyor.model.JobEvent;
import org.dm.conveyor.model.JobResultEvent;
import org.dm.conveyor.model.Transformer;
import org.dm.conveyor.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The JobExecutionService is the service class with business logic for the Job entity.
 * It provides methods to create, read and update check jobs and also methods
 * to update the state of a check job and to add results to a check job.
 *
 * JobService does not execute the jobs. It only manages their status.
 */
@Service
public class JobService {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final KafkaProducerService kafkaProducer;

    @Autowired
    public JobService( JobRepository jobRepository, KafkaProducerService kafkaProducer) {
        this.jobRepository = jobRepository;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Creates a new job with the given event.
     * The job is created with a unique UUID and the state CREATED.
     * A JobEvent is then sent to Kafka to start the execution of the job asynchronously.
     *
     * @param jobEvent The job event with the parameter of the job
     * @return The job with the state CREATED
     */
    public Job createJob(JobEvent jobEvent) {
        Job job = jobRepository.save(new Job(Job.StateEnum.CREATED, jobEvent.getId()));
        logStatus(job.getID(), job.getState());
        kafkaProducer.sendCheckEvent( null, jobEvent);
        return job;
    }

    /**
     * Returns the job with the given UUID.
     *
     * @param ID The UUID of the job
     * @return The job with the given UUID
     */
    public Optional<Job> getJob(String ID) {
        return jobRepository.findById(ID);
    }

    /**
     * Updates the given check job.
     *
     * @param job The check job to update
     * @return The updated check job
     */
    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    /**
     * Adds a result to the check job with the given UUID.
     *
     * @param ID The UUID of the job
     * @param jobResultEvent The check result event to add
     */
    public void addResult(String ID, JobResultEvent jobResultEvent) {
        Optional<Job> checkJobOptional = getJob(ID);
        if(checkJobOptional.isPresent()) {
            Job job = checkJobOptional.get();
            job.addResult(Transformer.createCheckResult(jobResultEvent));
            updateJob(job);
        }
    }

    /**
     * Updates the state of the check job with the given UUID.
     *
     * @param ID The UUID of the job
     * @param state The new state of the job
     */
    public void updateState(String ID, Job.StateEnum state) {
        Optional<Job> checkJobOptional = getJob(ID);
        if(checkJobOptional.isPresent()) {
            Job job = checkJobOptional.get();
            job.setState(state);
            updateJob(job);
            logStatus(ID, state);
        }
    }

    private void logStatus(String ID, Job.StateEnum status) {
        if (logger.isInfoEnabled()) {
            logger.info("Set status of check job {} to {}", ID, status);
        }
    }


}
