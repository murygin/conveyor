package org.dm.conveyor.rest;

import jakarta.servlet.http.HttpServletResponse;
import org.dm.conveyor.model.Job;
import org.dm.conveyor.model.JobEvent;
import org.dm.conveyor.model.JobNotFoundException;
import org.dm.conveyor.model.UUIDNotValidException;
import org.dm.conveyor.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * The JobController is the REST controller for the Conveyor application.
 * It provides two endpoints:
 * - POST /jobs: Creates a new job with the given job event
 * - GET /jobs/{id}: Returns the result of a job with the given UUID
 */
@RestController
public class JobController {

    public static final String URL_JOBS = "/jobs";

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Creates a new job with the given event.
     * The job is created with a unique UUID and the state CREATED.
     * After creation the job is returned with a status code 202 (ACCEPTED)
     * and the location header set to the URL of the job.
     *
     * @param jobEvent The job event with the details of the job to be created
     * @param response The HTTP response object
     * @return The job with the state CREATED
     */
    @PostMapping(URL_JOBS)
    Job create(@RequestBody JobEvent jobEvent, HttpServletResponse response) {
        if(jobEvent.getId()==null) {
            jobEvent.setId(UUID.randomUUID().toString());
        } else {
            validateUUID(jobEvent.getId());
        }
        response.setHeader("Location", String.format("%s/%s",URL_JOBS, jobEvent.getId()));
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return jobService.createJob(jobEvent);
    }

    /**
     * Returns the result of a job with the given UUID. The jobs are executed asynchronously.
     * Returned job contains a state. If the job has not yet been started, the state CREATED is returned.
     * If the job is currently running, the state RUNNING is returned. When the job is completed,
     * the state FINISHED and one or more results are returned.
     *
     * @param id The UUID of the job
     * @return The result of the job with a state and if the state is FINISHED with a result
     * @throws JobNotFoundException If the job with the given UUID does not exist
     */
    @GetMapping(URL_JOBS + "/{id}")
    Job get(@PathVariable String id) {
        return jobService.getJob(id)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    /**
     * Validates the given UUID. If the UUID is not valid, a UUIDNotValidException is thrown.
     *
     * @param id The UUID to validate
     */
    private void validateUUID(String id) {
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new UUIDNotValidException(e.getMessage());
        }
    }
}
