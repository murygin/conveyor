## Conveyor

![](conveyor.jpg)

This proof of concept shows the use of Spring Boot and Kafka together to execute long-running tasks asynchronously with a REST API.

### Built With

* [Java](https://en.wikipedia.org/wiki/Java_(programming_language))
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Apache Kafka](https://kafka.apache.org/)
* [H2 Database](https://www.h2database.com/html/main.html)
* [Docker](https://www.docker.com/)
* [Apache Maven](https://maven.apache.org/)

### Prerequisites

Kafka runs with [Docker Compose](https://docs.docker.com/compose/), which is integrated into Spring Boot. A working Docker setup must therefore be available to start the project. Java 21 and Maven are also required.

### Build & Run

1. Clone the repo
   ```sh
   git clone https://github.com/murygin/malware-scanner.git
   ```
2. Compile
   ```sh
   ./mvnw clean compile
   ```
3. Run
   ```sh
   ./mvnw spring-boot:run
   ```
   
### Usage

The API provides an endpoint for starting jobs and an endpoint for loading the job results. If the service is started with `./mvnw spring-boot:run` the base url is http://localhost:8080.

#### Bruno

[Bruno](https://www.usebruno.com/) is a Git-friendly, offline-first API client built for developers who want fast local workflows, plain-text collections, and better collaboration through Git.

You can use the Bruno collection in folder [src/test/bruno/Conveyor](./src/test/bruno/Conveyor) to test the API. 

#### `POST /jobs`

Starts ta new job. The job is started asynchronously. The result is not returned directly in the response. The response contains a confirmation of the start with the ID of the job. The response header `Location` contains the URL for loading the result.

Request:
```json
{
  "data": "conveyor.wait.ms=3500",
  "type": "slacker"
}
```
Response:
- Status: `202 Accepted`
- Header: `Location: /jobs/b3a5896f-387b-4363-a631-cfbf467db1ce`
```json
{
   "state": "CREATED",
   "results": [],
   "id": "b3a5896f-387b-4363-a631-cfbf467db1ce"
}
```

#### `GET /jobs/<UUID>`

Loads the result of a job. If the job has not yet been started, the status `CREATED` is returned. If the job is currently running, the status `RUNNING` is returned. When the job is completed, the status `FINISHED` and a result is returned.

Response:
- Status: `200 OK`
```json
{
   "state": "FINISHED",
   "results": [
      {
         "state": "OK",
         "name": "slacker",
         "details": "I finished this job in just 3503 ms, no problem"
      }
   ],
   "id": "b3a5896f-387b-4363-a631-cfbf467db1ce"
}
```

## How it works

The REST endpoint `POST /jobs` can be used to trigger a new job. When the endpoint is called, the method `create` is called in the controller. The Spring Boot REST Controller [o.d.c.rest.JobsController](./src/main/java/org/dm/conveyor/rest/JobController.java) contains the methods that are executed when the endpoints are called. The controller is only a facade and passes the calls on to the [o.d.c.service.JobService](src/main/java/org/dm/conveyor/service/JobService.java). 

If a new job is requested, the controller calls the method `createJob` in the `JobService`. The job is not started directly. The job is only triggered by the Kafka event. This has the advantage that the caller of the REST endpoint is not blocked and has to wait, but receives a response immediately. This method `createJob` in `JobService` creates a [o.d.c.model.Job](src/main/java/org/dm/conveyor/model/Job.java) with the status `CREATED` and saves it in the database. A [o.d.c.model.JobEvent](src/main/java/org/dm/conveyor/model/JobEvent.java) is then sent to [event streaming platform Kafka](https://kafka.apache.org/). 

The `jobEvents` are consumed by the [o.d.c.kafka.KafkaTopicListener](src/main/java/org/dm/conveyor/kafka/KafkaTopicListener.java). After receiving the event, the `KafkaTopicListener` set the status of the `Job` to `RUNNING` and starts the job by calling the `executeJob` method in the [o.d.c.service.JobExecutionService](src/main/java/org/dm/conveyor/service/JobExecutionService.java).

After the job is finished in the `JobExecutionService` is completed, an [o.d.c.model.JobResultEvent](src/main/java/org/dm/conveyor/model/JobResultEvent.java) is sent to Kafka. The `JobResultEvent` is consumed by the `KafkaTopicListener`. The `KafkaTopicListener` takes the result of the job from the event and saves it in the `Job` The status of the job is set to `FINISHED`. Now the result of the job can be loaded from the client via the REST endpoint `GET /jobs/<UUID>`.

## Improvements & Enhancements

- The service should only be able to be used if a client is authenticated.
- The API should be documented with Spring SpringDoc, OpenAPI and Swagger.
- Test coverage should be improved. Integration tests are to be implemented for the controller calls and the processing of Kafka events.
- A load test needs to be written to test how the system performs when many requests have to be processed simultaneously.
- Exception handling should be improved if an invalid request body is sent to the `POST /jobs` endpoint.

## Articles

With the articles in this section you can learn more about frameworks and systems that are used in this application.

**Kafka**
- [Apache Kafka Quickstart](https://kafka.apache.org/quickstart)
- [Run Kafka Streams Demo Application](https://kafka.apache.org/documentation/streams/quickstart)
- [Is a Key Required as Part of Sending Messages to Kafka?](https://www.baeldung.com/java-kafka-message-key)
- [What should I use as the key for my Kafka message?](https://forum.confluent.io/t/what-should-i-use-as-the-key-for-my-kafka-message/312/2)
- [Kafka Integration Testing with Spring Boot](https://tech4gods.com/kafka-integration-testing-with-spring-boot/)
- [Testing Kafka and Spring Boot](https://www.baeldung.com/spring-boot-kafka-testing)

**API Design**
- [REST API Design for Long-Running Tasks](https://restfulapi.net/rest-api-design-for-long-running-tasks/)

**Spring Boot**
- [Docker Compose Support in Spring Boot 3.1](https://spring.io/blog/2023/06/21/docker-compose-support-in-spring-boot-3-1)
- [Getting started with Spring Boot 3, Kafka over docker with docker-compose.yaml](https://www.geeksforgeeks.org/getting-started-with-spring-boot-3-kafka-over-docker-with-docker-composeyaml/)
- [Building REST services with Spring](https://spring.io/guides/tutorials/rest)
- [Spring Boot With H2 Database](https://www.baeldung.com/spring-boot-h2-database)
- [Building REST services with Spring](https://spring.io/guides/tutorials/rest)
- [Getting started with unit testing in spring boot](https://medium.com/javarevisited/getting-started-with-unit-testing-in-spring-boot-bada732a5baa)

## Contact

Daniel Murygin - [linkedin.com/in/murygin](https://www.linkedin.com/in/murygin/) - daniel.murygin@gmail.com

Project Link: [https://github.com/murygin/conveyor](https://github.com/murygin/malware-scanner)