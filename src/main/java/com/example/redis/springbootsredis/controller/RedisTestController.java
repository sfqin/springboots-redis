package com.example.redis.springbootsredis.controller;

import com.example.redis.springbootsredis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class RedisTestController {

    @Autowired
    private RedisService redisService;

    @GetMapping("get")
    public Object getStrKey(String key){
        log.info("key => {}",key);
        Object byStrKey = redisService.getByStrKey(key);
        log.info("value => {}",byStrKey);
        return byStrKey;
    }

    @GetMapping("lock1")
    public Object testLock1(String key){
        log.info("key => {}",key);
        boolean lockAndExpireV3 = redisService.getLockAndExpire(key, "value", 30);
        log.info("value => {}",lockAndExpireV3);
        return lockAndExpireV3;
    }

    @GetMapping("lock2")
    public Object testLock2(String key){
        log.info("key => {}",key);
        boolean lockAndExpireV3 = redisService.getLockAndExpireV2(key, "value", 30000);
        log.info("value => {}",lockAndExpireV3);
        return lockAndExpireV3;
    }


    @GetMapping("lock3")
    public Object testLock3(String key){
        log.info("key => {}",key);
        boolean lockAndExpireV3 = redisService.getLockAndExpireV3(key, "value", 30);
        log.info("value => {}",lockAndExpireV3);
        return lockAndExpireV3;
    }

    @GetMapping("lock4")
    public Object testLock4(String key){
        log.info("key => {}",key);
        boolean lockAndExpireV4 = redisService.getLockAndExpireV4(key, "value", 30);
        log.info("value => {}",lockAndExpireV4);
        return lockAndExpireV4;
    }

}
