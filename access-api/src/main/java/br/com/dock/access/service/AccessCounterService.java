package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
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

    public boolean isValid(){
        long current = redisClient.getLong(KEY);
        return current < accessLimit;
    }
}