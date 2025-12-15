package br.com.dock.access.client;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class RedisClient {

    private static final String SCRIPT_PATH = "src/main/resources/lua/increment_with_limit.lua";

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

    public long incrementWithLimit(String key, long limit) throws IOException {
        var resource = new ClassPathResource("lua/increment_with_limit.lua");
        String script;
        try (var in = resource.getInputStream()) {
            script = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        }

        return ((Number) redissonClient
                .getScript(StringCodec.INSTANCE)
                .eval(
                    RScript.Mode.READ_WRITE,
                    script,
                    RScript.ReturnType.INTEGER,
                    java.util.Collections.singletonList(key),
                    String.valueOf(limit)
                )).longValue();
    }
}
