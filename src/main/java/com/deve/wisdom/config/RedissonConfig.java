//-*- coding =utf-8 -*-
//@Time : 2023/8/19
//@Author: 邓闽川
//@File  RedissonConfig.java
//@software:IntelliJ IDEA
package com.deve.wisdom.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

       private Integer database;

       private String host;

       private Integer port;

       private String password;

       @Bean
        public RedissonClient getRedissonClient() {
           Config config = new Config();
           config.useSingleServer().setDatabase(database)
                   .setAddress("redis://"+host+":"+port)
                   .setPassword(password);
           return Redisson.create(config);

       }

}
