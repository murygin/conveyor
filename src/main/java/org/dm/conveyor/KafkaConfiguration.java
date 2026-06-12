package org.dm.conveyor;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

    public static final String TOPIC_CHECK_FILE = "topic-check-file";
    public static final String TOPIC_CHECK_RESULT = "topic-check-result";

    @Bean
    public NewTopic checkFileTopic() {
        return TopicBuilder.name(KafkaConfiguration.TOPIC_CHECK_FILE)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic checkResultTopic() {
        return TopicBuilder.name(KafkaConfiguration.TOPIC_CHECK_RESULT)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
