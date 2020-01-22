package com.example.redis.springbootsredis.service;

public interface RedisService {

    String getByStrKey(String key);

    /**
     * 尝试获取分布式锁
     * @param lockKey
     * @param lockValue
     * @param expireSecond 锁在多少秒后释放
     * @return
     */
    boolean getLockAndExpire(String lockKey, String lockValue,long expireSecond);

    /**
     * 尝试获取分布式锁
     * @param lockKey
     * @param lockValue
     * @param expireSecond
     * @return
     */
    boolean getLockAndExpireV2(String lockKey, String lockValue,long expireSecond);


    /**
     * 尝试获取分布式锁 (lua脚本实现)
     * @param lockKey
     * @param lockValue
     * @param expireSecond
     * @return
     */
    boolean getLockAndExpireV3(String lockKey, String lockValue,long expireSecond);

}
