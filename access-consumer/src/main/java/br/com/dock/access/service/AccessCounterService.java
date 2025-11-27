package br.com.dock.access.service;

import br.com.dock.access.client.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccessCounterService {

    private static final String KEY = "ACCESS_COUNT";

    @Value("${app.access-limit}")
    private long accessLimit;

    private final RedisClient redisClient;

    public AccessCounterService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public long increment() {
        long current = redisClient.increment(KEY);

        if (current > accessLimit) {
            redisClient.decrement(KEY);
            return -1;
        }

        return current;
    }

    public long getAccessLimit() {
        return accessLimit;
    }
}