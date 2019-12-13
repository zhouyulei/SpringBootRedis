package com.boot.debug.redis.server.controller;

import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.Item;
import com.boot.debug.redis.server.service.StringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字符串String实战-商品详情存储
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/29 20:58
 **/
@RestController
@RequestMapping("string")
public class StringController {

    private static final Logger log= LoggerFactory.getLogger(StringController.class);

    @Autowired
    private StringService stringService;


    //添加
    @RequestMapping(value = "put",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated Item item, BindingResult result){
        if (result.hasErrors()){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("--商品信息：{}",item);

            stringService.addItem(item);
        }catch (Exception e){
            log.error("--字符串String实战-商品详情存储-添加-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //获取详情
    @RequestMapping(value = "get",method = RequestMethod.GET)
    public BaseResponse get(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(stringService.getItem(id));

        }catch (Exception e){
            log.error("--字符串String实战-商品详情存储-添加-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}

















































