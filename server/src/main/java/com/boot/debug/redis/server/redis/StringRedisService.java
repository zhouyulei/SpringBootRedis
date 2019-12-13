package com.boot.debug.redis.server.redis;/**
 * Created by Administrator on 2019/10/29.
 */

import com.boot.debug.redis.server.enums.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/29 21:08
 **/
@Service
public class StringRedisService {

    private static final Logger log= LoggerFactory.getLogger(StringRedisService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations getOperation(){
        return stringRedisTemplate.opsForValue();
    }

    public void put(final String key,final String value) throws Exception{
        getOperation().set(Constant.RedisStringPrefix+key,value);
    }

    public Object get(final String key) throws Exception{
        return getOperation().get(Constant.RedisStringPrefix+key);
    }

    public Boolean exist(final String key) throws Exception{
        return stringRedisTemplate.hasKey(Constant.RedisStringPrefix+key);
    }

    public void expire(final String key,final Long expireSeconds) throws Exception{
        stringRedisTemplate.expire(key,expireSeconds, TimeUnit.SECONDS);
    }
}

































