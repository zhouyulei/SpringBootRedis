package com.boot.debug.redis.server.service;

import com.boot.debug.redis.model.entity.Problem;
import com.boot.debug.redis.model.mapper.ProblemMapper;
import com.boot.debug.redis.server.enums.Constant;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 问题库服务
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 23:12
 **/
@Service
public class ProblemService {

    private static final Logger log= LoggerFactory.getLogger(ProblemService.class);

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //TODO:项目启动拉取出数据库中的问题库，并塞入缓存Set集合中
    @PostConstruct
    public void init(){
        initDBToCache();
    }

    //TODO:拉取出数据库中的所有问题库，并塞入缓存Set集合中
    private void initDBToCache(){
        try {
            //redisTemplate.delete(Arrays.asList(Constant.RedisSetProblemKey,Constant.RedisSetProblemsKey));

            SetOperations<String,Problem> setOperations=redisTemplate.opsForSet();
            Set<Problem> set=problemMapper.getAll();
            if (set!=null && !set.isEmpty()){
                set.forEach(problem -> setOperations.add(Constant.RedisSetProblemKey,problem));
                set.forEach(problem -> setOperations.add(Constant.RedisSetProblemsKey,problem));
            }
        }catch (Exception e){
            log.error("项目启动拉取出数据库中的问题库，并塞入缓存Set集合中-发生异常：",e.fillInStackTrace());
        }
    }

    //TODO:从缓存中获取随机的问题
    public Problem getRandomEntity(){
        Problem problem=null;
        try {
            SetOperations<String,Problem> setOperations=redisTemplate.opsForSet();
            Long size=setOperations.size(Constant.RedisSetProblemKey);
            if (size>0){
                problem=setOperations.pop(Constant.RedisSetProblemKey);
            }else{
                this.initDBToCache();
                problem=setOperations.pop(Constant.RedisSetProblemKey);
            }
        }catch (Exception e){
            log.error("从缓存中获取随机的问题-发生异常：",e.fillInStackTrace());
        }
        return problem;
    }




    //TODO:从缓存中获取随机的、乱序的试题列表
    public Set<Problem> getRandomEntitys(Integer total){
        Set<Problem> problems=Sets.newHashSet();
        try {
            SetOperations<String,Problem> setOperations=redisTemplate.opsForSet();
            problems=setOperations.distinctRandomMembers(Constant.RedisSetProblemsKey,total);

            //SetOperations<String,Problem> setOperations=redisTemplate.opsForSet();
            //problems=setOperations.members(Constant.RedisSetProblemsKey);

            //List list=Collections.shuffle(redisTemplate.opsForList().range());

        }catch (Exception e){
            log.error("从缓存中获取随机的、乱序的试题列表-发生异常：",e.fillInStackTrace());
        }
        return problems;
    }
}























