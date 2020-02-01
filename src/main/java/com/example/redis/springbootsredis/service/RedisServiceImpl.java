package com.example.redis.springbootsredis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public String getByStrKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * 原生的 jediscommand 实现方式
     * 注：高版本和低版本使用有一点点差别
     * @param lockKey
     * @param lockValue
     * @param expireSecond 锁在多少秒后释放
     * @return
     */
    @Override
    public boolean getLockAndExpire(final String lockKey, final String lockValue, long expireSecond) {

        if(StringUtils.isEmpty(lockKey) || lockValue == null){
            log.error("加锁参数错误 key：{}，value:{}", lockKey,lockValue);
            return false;
        }
        Boolean execute = (Boolean) redisTemplate.execute((RedisCallback) connection -> {
            JedisCommands nativeConnection = (JedisCommands) connection.getNativeConnection();
            //返回 OK 或者 null
            SetParams setParams = SetParams.setParams();
            setParams.nx().ex((int)expireSecond);
            String lockFlag = nativeConnection.set(lockKey, lockValue, setParams);
            return lockFlag != null;
        });
        return execute;
    }

    /**
     * 2.1的包开始
     * 高版本直接 setnx setex 连用
     * @param lockKey
     * @param lockValue
     * @param expireSecond
     * @return
     */
    @Override
    public boolean getLockAndExpireV2(String lockKey, String lockValue, long expireSecond) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey,lockKey,expireSecond, TimeUnit.SECONDS);
    }

    /**
     * redis 2.6.0 开始支持lua脚本
     * @param lockKey
     * @param lockValue
     * @param expireSecond
     * @return
     */
    @Override
    public boolean getLockAndExpireV3(String lockKey, String lockValue, long expireSecond) {

        DefaultRedisScript<Boolean> booleanDefaultRedisScript = new DefaultRedisScript<>();

        booleanDefaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lock.lua")));

        booleanDefaultRedisScript.setResultType(Boolean.class);
        //封装参数
        List<String> param = new ArrayList<>();
        param.add(lockKey);
        param.add(lockValue);
        param.add(String.valueOf(expireSecond));

        return redisTemplate.execute(booleanDefaultRedisScript,param);
    }

    /**
     * 原生 jedis 实现分布式锁
     * @param lockKey
     * @param lockValue
     * @param expireSecond
     * @return
     */
    @Override
    public boolean getLockAndExpireV4(String lockKey, String lockValue, long expireSecond) {

        Jedis jedis = jedisPool.getResource();
        SetParams setParams = SetParams.setParams();
        setParams.nx().ex((int)expireSecond);
        String result = jedis.set(lockKey, lockValue,setParams);

        return result != null;
    }


}
