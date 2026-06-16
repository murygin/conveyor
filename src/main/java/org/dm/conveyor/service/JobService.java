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
 * It provides methods to create, read and update jobs and also methods
 * to update the state of a job and to add results to a job.
 * <p>
 * JobService does not execute the jobs. It only manages their status.
 */
@Service
public class JobService {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final KafkaProducerService kafkaProducer;

    @Autowired
    public JobService(JobRepository jobRepository, KafkaProducerService kafkaProducer) {
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
        logStatus(job.getId(), job.getState());
        kafkaProducer.sendJobEvent(null, jobEvent);
        return job;
    }

    /**
     * Returns the job with the given UUID.
     *
     * @param id The UUID of the job
     * @return The job with the given UUID
     */
    public Optional<Job> getJob(String id) {
        return jobRepository.findById(id);
    }

    /**
     * Updates the given job.
     *
     * @param job The job to update
     * @return The updated job
     */
    @SuppressWarnings("UnusedReturnValue")
    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    /**
     * Adds a result to the job with the given UUID.
     *
     * @param id             The UUID of the job
     * @param jobResultEvent The result event to add
     */
    public void addResult(String id, JobResultEvent jobResultEvent) {
        Optional<Job> jobOptional = getJob(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.addResult(Transformer.createJobResult(jobResultEvent));
            updateJob(job);
        }
    }

    /**
     * Updates the state of the job with the given UUID.
     *
     * @param id    The UUID of the job
     * @param state The new state of the job
     */
    public void updateState(String id, Job.StateEnum state) {
        Optional<Job> jobOptional = getJob(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setState(state);
            updateJob(job);
            logStatus(id, state);
        }
    }

    private void logStatus(String id, Job.StateEnum status) {
        if (logger.isInfoEnabled()) {
            logger.info("Set status of job {} to {}", id, status);
        }
    }


}
