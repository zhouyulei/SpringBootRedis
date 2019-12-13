package com.boot.debug.redis.server.controller;

import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.Item;
import com.boot.debug.redis.server.service.CacheFightService;
import com.boot.debug.redis.server.service.StringService;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 缓存典型应用场景实战
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/11/1 11:42
 **/
@RestController
@RequestMapping("cache/fight")
public class CacheFightController {

    private static final Logger log= LoggerFactory.getLogger(CacheFightController.class);

    @Autowired
    private CacheFightService cacheFightService;

    //TODO:限流组件
    private static final RateLimiter LIMITER=RateLimiter.create(1);


    //缓存穿透
    @RequestMapping(value = "through",method = RequestMethod.GET)
    public BaseResponse get(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            //response.setData(cacheFightService.getItem(id));

            //response.setData(cacheFightService.getItemV2(id));

            //TODO:实际应用场景中 - 限流（组件-hystrix、guava提供的RateLimiter）
            //TODO:实际应用场景中 - 限流：guava提供的RateLimiter，尝试获取令牌：此处是单线程服务的限流,内部采用令牌捅算法实现
            if (LIMITER.tryAcquire()){
            //if (LIMITER.tryAcquire(10L, TimeUnit.SECONDS)){
                response.setData(cacheFightService.getItemV3(id));
            }
        }catch (Exception e){
            log.error("--典型应用场景实战-缓存穿透-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    //缓存击穿
    @RequestMapping(value = "through/beat",method = RequestMethod.GET)
    public BaseResponse getBeat(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(cacheFightService.getItemCacheBeat(id));

        }catch (Exception e){
            log.error("--典型应用场景实战-缓存击穿-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}

















































