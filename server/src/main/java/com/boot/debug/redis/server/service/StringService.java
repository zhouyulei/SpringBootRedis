package com.boot.debug.redis.server.service;/**
 * Created by Administrator on 2019/10/29.
 */

import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.model.entity.Item;
import com.boot.debug.redis.model.mapper.ItemMapper;
import com.boot.debug.redis.server.redis.StringRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/29 21:05
 **/
@Service
public class StringService {

    private static final Logger log= LoggerFactory.getLogger(StringService.class);

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StringRedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;


    //添加商品
    @Transactional(rollbackFor = Exception.class)
    public Integer addItem(Item item) throws Exception{
        item.setCreateTime(new Date());
        item.setId(null);
        itemMapper.insertSelective(item);
        Integer id=item.getId();

        //保证缓存-数据库双写的一致性
        if (id>0){
            redisService.put(id.toString(),objectMapper.writeValueAsString(item));
        }
        return id;
    }

    //获取商品
    public Item getItem(Integer id) throws Exception{
        Item item=null;
        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();
                log.info("---string数据类型，从缓存中取出来的value：{}",result);
                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---string数据类型，从数据库查询：id={}",id);

                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }
            }
        }
        return item;
    }
}
























