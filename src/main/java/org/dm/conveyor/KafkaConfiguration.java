package org.dm.conveyor;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    public static final String TOPIC_JOB = "topic-job";
    public static final String TOPIC_JOB_RESULT = "topic-job-result";

    @Bean
    public NewTopic jobTopic() {
        return TopicBuilder.name(KafkaConfiguration.TOPIC_JOB)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic jobResultTopic() {
        return TopicBuilder.name(KafkaConfiguration.TOPIC_JOB_RESULT)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
