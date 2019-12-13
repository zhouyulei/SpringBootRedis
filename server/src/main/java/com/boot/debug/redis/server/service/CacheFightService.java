package com.boot.debug.redis.server.service;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.model.entity.Item;
import com.boot.debug.redis.model.mapper.ItemMapper;
import com.boot.debug.redis.server.enums.Constant;
import com.boot.debug.redis.server.redis.StringRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.BloomFilter;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 典型应用场景实战
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/11/1 11:44
 **/
@Service
public class CacheFightService {

    private static final Logger log= LoggerFactory.getLogger(CacheFightService.class);

    private static final Snowflake SNOWFLAKE=new Snowflake(3,2);

    @Autowired
    private StringRedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemMapper itemMapper;




    //TODO:缓存穿透-查询商品
    public Item getItem(Integer id) throws Exception{
        Item item=null;
        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();
                //log.info("---缓存穿透，从缓存中查询---");

                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---缓存穿透，从数据库查询：id={}",id);

                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }
            }
        }
        return item;
    }


    //缓存穿透-查询商品-解决方法
    public Item getItemV2(Integer id) throws Exception{
        Item item=null;

        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();

                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---缓存穿透，从数据库查询：id={}",id);

                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }else{
                    redisService.put(id.toString(),"");
                    redisService.expire(id.toString(),3600L);
                }
            }
        }
        return item;
    }



    //缓存穿透-查询商品-解决方法 - 限流
    public Item getItemV3(Integer id) throws Exception{
        Item item=null;

        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();

                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---guava提供的RateLimiter，获取到令牌-缓存穿透，从数据库查询：id={}",id);

                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }else{
                    //TODO:connection reset.....
                    redisService.put(id.toString(),"");
                    redisService.expire(id.toString(),3600L);
                }

            }
        }
        return item;
    }


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //TODO:缓存击穿-查询商品详情
    public Item getItemCacheBeat(Integer id) throws Exception{
        Item item=null;

        if (redisService.exist(id.toString())){
            String result=redisService.get(id.toString()).toString();

            if (StrUtil.isNotBlank(result)){
                item=objectMapper.readValue(result,Item.class);
            }
        }else{
            //TODO:分布式锁的实现 - 同一时刻只能保证 拥有该key 的一个线程进入 执行共享的业务代码
            //TODO:SETNX、EXPIRE、DELETE ； Redis单线程（它的操作是原子性操作） - io多路复用（单核cpu 却支持多任务、多用户的使用，频繁切换很快，用户识别不了）
            String value=SNOWFLAKE.nextIdStr();
            ValueOperations<String,String> valueOperations=stringRedisTemplate.opsForValue();
            Boolean lock=valueOperations.setIfAbsent(Constant.RedisCacheBeatLockKey,value);
            try {
                if (lock){
                    stringRedisTemplate.expire(Constant.RedisCacheBeatLockKey,10L,TimeUnit.SECONDS);

                    log.info("---缓存击穿，从数据库查询：id={}",id);
                    item=itemMapper.selectByPrimaryKey(id);
                    if (item!=null){
                        redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                    }else{
                        //TODO:connection reset.....
                        redisService.put(id.toString(),"");
                        redisService.expire(id.toString(),3600L);
                    }

                }
            }finally {
                String currValue=valueOperations.get(Constant.RedisCacheBeatLockKey);
                if (StrUtil.isNotBlank(currValue) && currValue.equals(value)){
                    stringRedisTemplate.delete(Constant.RedisCacheBeatLockKey);
                }
            }

        }
        return item;
    }

}
































