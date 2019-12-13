package com.boot.debug.redis.server.service;

import com.boot.debug.redis.model.entity.Item;
import com.boot.debug.redis.model.mapper.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 商品详情服务service
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/11/3 15:07
 **/
@Service
public class ItemService {

    private static final Logger log= LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemMapper itemMapper;

    //TODO:value-必填；key-支持 springEL表达式
    //TODO:java.lang.ClassCastException - 因为devtools为了实现重新装载class自己实现了一个类加载器，所以会导致类型强转异常
    //TODO:缓存存在时，则直接取缓存，不存在，走方法体的逻辑
    @Cacheable(value = "SpringBootRedis:Item",key = "#id",unless = "#result == null")
    public Item getInfo(Integer id){
        Item entity=itemMapper.selectByPrimaryKey(id);

        log.info("--@Cacheable走数据库查询：{}",entity);
        return entity;
    }

    //TODO:不管你缓存存不存在，都会put到缓存中去
    @CachePut(value = "SpringBootRedis:Item",key = "#id",unless = "#result == null")
    public Item getInfoV2(Integer id){
        Item entity=itemMapper.selectByPrimaryKey(id);

        entity.setCode(UUID.randomUUID().toString());

        log.info("--@CachePut走数据库查询：{}",entity);
        return entity;
    }

    //TODO：失效/删除缓存
    @CacheEvict(value = "SpringBootRedis:Item",key = "#id")
    public void delete(Integer id){
        itemMapper.deleteByPrimaryKey(id);

        log.info("--@CacheEvict删除功能：id={}",id);
    }
}























