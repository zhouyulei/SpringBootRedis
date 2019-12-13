package com.boot.debug.redis.server.controller;/**
 * Created by Administrator on 2019/10/29.
 */

import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/29 15:47
 **/
@RestController
@RequestMapping("base")
public class BaseController {

    private static final Logger log= LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String RedisHelloWorldKey="SpringBootRedis:HelloWorld";

    @RequestMapping(value = "info",method = RequestMethod.GET)
    public String info(){
        String name="Redis技术入门与典型应用场景实战(基于SpringBoot2.x)";
        return name;
    }

    @RequestMapping(value = "/hello/world/put",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse helloWorldPut(@RequestParam String helloName){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            stringRedisTemplate.opsForValue().set(RedisHelloWorldKey,helloName);
            response.setData("hello world!");
        }catch (Exception e){
            log.info("--hello world get异常信息： ",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/hello/world/get",method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse helloWorldGet(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            String result=stringRedisTemplate.opsForValue().get(RedisHelloWorldKey);
            response.setData(result);
        }catch (Exception e){
            log.info("--hello world get异常信息： ",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}






























