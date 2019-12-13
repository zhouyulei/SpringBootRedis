package com.boot.debug.redis.server.service;/**
 * Created by Administrator on 2019/10/30.
 */

import com.boot.debug.redis.model.entity.Notice;
import com.boot.debug.redis.model.entity.Product;
import com.boot.debug.redis.model.mapper.NoticeMapper;
import com.boot.debug.redis.model.mapper.ProductMapper;
import com.boot.debug.redis.server.enums.Constant;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 列表List服务
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 9:48
 **/
@Service
public class ListService {

    public static final Logger log= LoggerFactory.getLogger(ListService.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //添加商品
    @Transactional(rollbackFor = Exception.class)
    public Integer addProduct(Product product) throws Exception{
        if (product!=null){
            product.setId(null);
            productMapper.insertSelective(product);
            Integer id=product.getId();

            if (id>0){
                this.pushRedisService(product);
            }
            return id;
        }
        return -1;
    }

    //获取历史发布的商品列表
    public List<Product> getHistoryProducts(final Integer userId) throws Exception{
        List<Product> list= Lists.newLinkedList();

        ListOperations<String,Product> listOperations=redisTemplate.opsForList();

        final String key=Constant.RedisListPrefix+userId;
        //TODO:倒序->userID=10010 ->Rabbitmq入门与实战,Redis入门与实战,SpringBoot项目实战
        //list=listOperations.range(key,0,listOperations.size(key));
        //log.info("--倒序：{}",list);

        //TODO:顺序->userID=10010 ->SpringBoot项目实战,Redis入门与实战,Rabbitmq入门与实战
        //Collections.reverse(list);
        //log.info("--顺序：{}",list);

        //TODO:弹出来移除的方式
        Product entity=listOperations.rightPop(key);
        while (entity!=null){
            list.add(entity);

            entity=listOperations.rightPop(key);
        }
        return list;
    }


    @Autowired
    private NoticeMapper noticeMapper;

    //创建通告
    @Transactional(rollbackFor = Exception.class)
    public void pushNotice(Notice notice) throws Exception{
        if (notice!=null){
            notice.setId(null);
            noticeMapper.insertSelective(notice);
            final Integer id=notice.getId();

            if (id>0){
                //TODO:塞入List列表中(队列)，准备被拉取异步通知至不同的商户的邮箱 - applicationEvent&Listener;Rabbitmq;jms
                ListOperations<String,Notice> listOperations=redisTemplate.opsForList();
                listOperations.leftPush(Constant.RedisListNoticeKey,notice);
            }
        }
    }



    //TODO:往缓存中塞信息-可以抽取到ListRedisService
    private void pushRedisService(final Product product) throws Exception{
        ListOperations<String,Product> listOperations=redisTemplate.opsForList();
        listOperations.leftPush(Constant.RedisListPrefix+product.getUserId(),product);
    }
}































