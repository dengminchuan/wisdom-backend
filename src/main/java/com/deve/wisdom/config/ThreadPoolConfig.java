//-*- coding =utf-8 -*-
//@Time : 2023/8/20
//@Author: 邓闽川
//@File  ThreadPoolConfig.java
//@software:IntelliJ IDEA
package com.deve.wisdom.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
@ConfigurationProperties(prefix = "thread.pool")
@Data
public class ThreadPoolConfig {

    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000,true),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

}
