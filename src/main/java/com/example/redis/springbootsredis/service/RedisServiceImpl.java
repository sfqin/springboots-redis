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
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String getByStrKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }


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

    //2.6.0

    @Override
    public boolean getLockAndExpireV2(String lockKey, String lockValue, long expireSecond) {



        return false;
    }

    @Override
    public boolean getLockAndExpireV3(String lockKey, String lockValue, long expireSecond) {

        DefaultRedisScript<Boolean> booleanDefaultRedisScript = new DefaultRedisScript<>();

        booleanDefaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("add.lua")));

        booleanDefaultRedisScript.setResultType(Boolean.class);
        //封装参数
        List<String> param = new ArrayList<>();
        param.add(lockKey);
        param.add(lockValue);


        return redisTemplate.execute(booleanDefaultRedisScript,param,expireSecond+"");
    }

}
