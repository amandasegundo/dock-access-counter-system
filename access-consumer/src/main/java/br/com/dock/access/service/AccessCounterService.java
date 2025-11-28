package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
import br.com.dock.access.dto.AccessEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessCounterService {

    private static final String KEY = "ACCESS_COUNT";

    @Value("${app.access-limit}")
    private long accessLimit;

    private final RedisClient redisClient;

    private static final Logger log = LoggerFactory.getLogger(AccessCounterService.class);

    public AccessCounterService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void process(AccessEventMessage message){
        log.info("Message received successfully: requestId [{}]", message.getRequestId());

        long count = increment();

        if (count == -1L) {
            log.warn("Access limit reached [{}]", accessLimit);
        } else {
            log.info("Access counted, current count [{}]", count);
        }
    }

    private long increment() {
        long current = redisClient.increment(KEY);

        if (current > accessLimit) {
            redisClient.decrement(KEY);
            return -1;
        }

        return current;
    }
}