package br.com.dock.access.client;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisClient {

    @Autowired
    private RedissonClient redissonClient;

    public long increment(String key) {
        RAtomicLong atomic = redissonClient.getAtomicLong(key);
        return atomic.incrementAndGet();
    }

    public long getLong(String key) {
        RAtomicLong atomic = redissonClient.getAtomicLong(key);
        return atomic.get();
    }
}
