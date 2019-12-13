package com.boot.debug.redis.server.scheduler;/**
 * Created by Administrator on 2019/10/30.
 */

import com.boot.debug.redis.model.entity.Notice;
import com.boot.debug.redis.model.entity.User;
import com.boot.debug.redis.model.mapper.UserMapper;
import com.boot.debug.redis.server.enums.Constant;
import com.boot.debug.redis.server.service.EmailService;
import com.boot.debug.redis.server.thread.NoticeThread;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis列表-队列的消费者监听器
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 14:51
 **/
@Component
@EnableScheduling
public class ListListenerScheduler {

    private static final Logger log= LoggerFactory.getLogger(ListListenerScheduler.class);

    private static final String listenKey= Constant.RedisListNoticeKey;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    //TODO：近实时的定时任务检测
    //@Scheduled(cron = "0/10 * * * * ?")
    @Scheduled(cron = "0/59 * * * * ?")
    public void schedulerListenNotice(){
        log.info("----定时任务调度队列监听、检测通告消息，监听list中的数据");

        ListOperations<String,Notice> listOperations=redisTemplate.opsForList();
        Notice notice=listOperations.rightPop(listenKey);
        while (notice!=null){
            //TODO:发送给到所有的商户的邮箱
            this.noticeUser(notice);

            notice=listOperations.rightPop(listenKey);
        }
    }

    //TODO:发送通知给到不同的商户
    @Async("threadPoolTaskExecutor")
    private void noticeUser(Notice notice){
        if (notice!=null){
            List<User> list=userMapper.selectList();

            //TODO:写法一-java8 stream api触发
            /*if (list!=null && !list.isEmpty()){
                list.forEach(user -> emailService.emailUserNotice(notice,user));
            }*/


            //TODO:写法二-线程池/多线程触发
            try {
                if (list!=null && !list.isEmpty()){
                    ExecutorService executorService=Executors.newFixedThreadPool(4);
                    List<NoticeThread> threads= Lists.newLinkedList();

                    list.forEach(user -> {
                        threads.add(new NoticeThread(user,notice,emailService));
                    });

                    executorService.invokeAll(threads);
                }
            }catch (Exception e){
                log.error("近实时的定时任务检测-发送通知给到不同的商户-法二-线程池/多线程触发-发生异常：",e.fillInStackTrace());
            }
        }
    }


}























