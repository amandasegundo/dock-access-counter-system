package br.com.dock.access.kafka;

import br.com.dock.access.dto.AccessEventMessage;
import br.com.dock.access.service.AccessCounterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final AccessCounterService accessCounterService;

    public KafkaEventConsumer(AccessCounterService accessCounterService) {
        this.objectMapper = new ObjectMapper();
        this.accessCounterService = accessCounterService;
    }

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String messageJson) {
        try {
            AccessEventMessage message = objectMapper.readValue(messageJson, AccessEventMessage.class);
            accessCounterService.process(message);
        } catch (Exception e) {
            log.error("Error processing message: [{}]", messageJson, e);
        }
    }
}
