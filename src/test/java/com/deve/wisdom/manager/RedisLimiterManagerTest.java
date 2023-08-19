package com.deve.wisdom.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Test
    void testRateLimits() throws Exception {
        for(int i=0;i<10;i++){
            redisLimiterManager.doRateLimite("1");
            System.out.println("success");
        }

    }
}