package org.dm.conveyor.kafka;

import org.dm.conveyor.KafkaConfiguration;
import org.dm.conveyor.model.JobEvent;
import org.dm.conveyor.model.JobResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;


    public void sendCheckEvent(String key, JobEvent value) {
        sendEvent(KafkaConfiguration.TOPIC_CHECK_FILE, key, value);
    }

    public void sendResultEvent(String key, JobResultEvent value) {
        sendEvent(KafkaConfiguration.TOPIC_CHECK_RESULT, key, value);
    }

    private <T> void sendEvent(String topic, String key, T value) {
        var future = kafkaTemplate.send(topic, key, value);
        future.whenComplete((sendResult, exception) -> {
            if (exception != null) {
                future.completeExceptionally(exception);
            } else {
                future.complete(sendResult);
            }
            LOGGER.info("Event send to Kafka topic {}: {}", topic, value);
        });
    }
}
