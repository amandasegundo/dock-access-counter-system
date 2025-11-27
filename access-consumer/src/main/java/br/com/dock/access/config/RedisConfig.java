package br.com.dock.access.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${redisson.config}")
    private String redissonYamlConfig;

    @Bean
    public RedissonClient redissonClient() throws Exception {
        Config config = Config.fromYAML(redissonYamlConfig);
        return Redisson.create(config);
    }
}
