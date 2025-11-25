package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducer.class);

    @Value("${spring.kafka.producer.output.destination}")
    private String output;

    private final KafkaTemplate<String, AccessEventMessage> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, AccessEventMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processMessage(final AccessEventMessage payload) {
        try {
            var message = buildMessage(payload);

            log.info("Sending message [{}] to topic [{}]", payload, output);
            kafkaTemplate.send(message);

            log.info("Message sent successfully [{}]", payload);
        } catch (KafkaException e) {
            log.error("Failed to send message to Kafka [{}]", payload);
            throw e;
        }
    }

    private Message<AccessEventMessage> buildMessage(final AccessEventMessage payload) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, output)
                .build();
    }
}
