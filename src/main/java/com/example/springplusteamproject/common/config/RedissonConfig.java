package com.example.springplusteamproject.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {

        Dotenv dotenv = Dotenv.load();

        String redisHost = dotenv.get("REDIS_HOST", "127.0.0.1");

        String redisPort = dotenv.get("REDIS_PORT", "6379");

        Config config = new Config();

        config.useSingleServer()
            .setAddress("redis://" + redisHost + ":" + redisPort);
        return Redisson.create(config);
    }
}
