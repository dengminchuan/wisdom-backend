//-*- coding =utf-8 -*-
//@Time : 2023/8/19
//@Author: 邓闽川
//@File  RedisLimiter.java
//@software:IntelliJ IDEA
package com.deve.wisdom.manager;

import com.deve.wisdom.common.ErrorCode;
import com.deve.wisdom.exception.BusinessException;
import com.deve.wisdom.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 区分不同的id
     * @param key
     */

    public void doRateLimite(String key){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //1秒产生5个令牌
        rateLimiter.trySetRate(RateType.OVERALL,5,1, RateIntervalUnit.SECONDS);
        //会员可以增加次数
        boolean canOp = rateLimiter.tryAcquire(1);
        if(!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }


}
