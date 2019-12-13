package com.boot.debug.redis.server.redis;

import com.boot.debug.redis.model.entity.SysConfig;
import com.boot.debug.redis.model.entity.User;
import com.boot.debug.redis.model.mapper.SysConfigMapper;
import com.boot.debug.redis.server.enums.Constant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.crypto.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * hash缓存服务
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/31 21:10
 **/
@Service
public class HashRedisService {

    private static final Logger log= LoggerFactory.getLogger(HashRedisService.class);

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //TODO:实时获取所有有效的数据字典列表-转化为map-存入hash缓存中
    @Async
    public void cacheConfigMap(){
        try {
            List<SysConfig> configs=sysConfigMapper.selectActiveConfigs();
            if (configs!=null && !configs.isEmpty()){
                Map<String,List<SysConfig>> dataMap= Maps.newHashMap();

                //TODO:所有的数据字典列表遍历 -> 转化为 hash存储的map
                configs.forEach(config -> {
                    List<SysConfig> list=dataMap.get(config.getType());
                    if (list==null || list.isEmpty()){
                        list= Lists.newLinkedList();
                    }
                    list.add(config);

                    dataMap.put(config.getType(),list);
                });

                //TODO:存储到缓存hash中
                HashOperations<String,String,List<SysConfig>> hashOperations=redisTemplate.opsForHash();
                hashOperations.putAll(Constant.RedisHashKeyConfig,dataMap);
            }
        }catch (Exception e){
            log.error("实时获取所有有效的数据字典列表-转化为map-存入hash缓存中-发生异常：",e.fillInStackTrace());
        }
    }

    //TODO:从缓存hash中获取所有的数据字典配置map
    public Map<String,List<SysConfig>> getAllCacheConfig(){
        Map<String,List<SysConfig>> map=Maps.newHashMap();
        try {
            HashOperations<String,String,List<SysConfig>> hashOperations=redisTemplate.opsForHash();
            map=hashOperations.entries(Constant.RedisHashKeyConfig);
        }catch (Exception e){
            log.error("从缓存hash中获取所有的数据字典配置map-发生异常：",e.fillInStackTrace());
        }
        return map;
    }

    //TODO:从缓存hash中获取特定的数据字典列表
    public List<SysConfig> getCacheConfigByType(final String type){
        List<SysConfig> list=Lists.newLinkedList();
        try {
            HashOperations<String,String,List<SysConfig>> hashOperations=redisTemplate.opsForHash();
            list=hashOperations.get(Constant.RedisHashKeyConfig,type);
        }catch (Exception e){
            log.error("从缓存hash中获取特定的数据字典列表-发生异常：",e.fillInStackTrace());
        }
        return list;
    }


    /*public void method(final String type) {
        HashOperations<String, String, User > hashOperations = redisTemplate.opsForHash();
    }*/
}































