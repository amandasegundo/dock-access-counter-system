package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.dto.AccessEventResponse;
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

    public AccessEventResponse processMessage(final AccessEventMessage payload) {
        AccessEventResponse response = new AccessEventResponse();

        try {
            log.info("Message with requestId [{}] received", payload.getRequestId());
            var message = buildMessage(payload);

            log.info("Sending message with requestId [{}] to topic [{}]", payload.getRequestId(), output);
            kafkaTemplate.send(message);

            log.info("Message with RequestId [{}] sent successfully", payload.getRequestId());
            response.setSuccess(true);
            response.setMessage("Message processed successfully.");

        } catch (KafkaException e) {
            log.error("Failed to send message to Kafka [{}]", payload, e);
            response.setSuccess(false);
            response.setMessage("Error processing message.");
        }
        return response;
    }

    private Message<AccessEventMessage> buildMessage(final AccessEventMessage payload) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, output)
                .build();
    }
}
