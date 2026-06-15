package org.dm.conveyor.rest.test;

import org.dm.conveyor.ConveyorApplication;
import org.dm.conveyor.kafka.KafkaTopicListener;
import org.dm.conveyor.model.Job;
import org.dm.conveyor.model.JobEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;


import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ConveyorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class JobControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    RestTestClient client;

    @Autowired
    KafkaTopicListener kafkaTopicListener;

    @Test
    void testStartJobWithoutParameter() throws InterruptedException {
        kafkaTopicListener.resetLatches();
        Job job = client.post()
                .uri("/jobs")
                .body(new JobEvent())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Job.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(job);
        checkJobResponse(job, Job.StateEnum.CREATED);
        assertTrue(kafkaTopicListener.getCountDownLatchJob().await(2, TimeUnit.SECONDS), String.format("Timeout while waiting for start of job %s", job.getId()));
        assertTrue(kafkaTopicListener.getCountDownLatchResult().await(2, TimeUnit.SECONDS), String.format("Timeout while waiting for result of job %s", job.getId()));
    }

    @Test
    void test500msJob() throws InterruptedException {
        kafkaTopicListener.resetLatches();
        JobEvent event = new JobEvent();
        event.setData("conveyor.wait.ms=500");

        Job job = client.post()
                .uri("/jobs")
                .body(event)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Job.class)
                .returnResult()
                .getResponseBody();

        checkJobResponse(job, Job.StateEnum.CREATED);
        assertTrue(kafkaTopicListener.getCountDownLatchJob().await(2, TimeUnit.SECONDS));
        assertNotNull(job);

        String id = job.getId();
        checkJobResponse(getResponseForJob(id), Job.StateEnum.RUNNING);

        assertTrue(kafkaTopicListener.getCountDownLatchResult().await(2, TimeUnit.SECONDS), String.format("Timeout while waiting for result of job %s", id));
        checkJobResponse(getResponseForJob(id), Job.StateEnum.FINISHED);
    }

    private void checkJobResponse(Job job, Job.StateEnum expectedState) {
        assertNotNull(job);
        assertEquals(expectedState, job.getState(), String.format("Status of job %s is not %s", job.getId(), expectedState));
        assertNotNull(job.getId());
    }

    private Job getResponseForJob(String jobId) {
        EntityExchangeResult<Job> result = client.get()
                .uri("/jobs/" + jobId)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<Job>() {
                })
                .returnResult();
        return result.getResponseBody();
    }
}
