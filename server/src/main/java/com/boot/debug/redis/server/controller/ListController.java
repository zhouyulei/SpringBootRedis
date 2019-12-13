package com.boot.debug.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.Item;
import com.boot.debug.redis.model.entity.Notice;
import com.boot.debug.redis.model.entity.Product;
import com.boot.debug.redis.server.service.ListService;
import com.boot.debug.redis.server.service.StringService;
import com.boot.debug.redis.server.utils.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 列表List实战-商户商品列表存储
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 20:58
 **/
@RestController
@RequestMapping("list")
public class ListController extends AbstractController{

    @Autowired
    private ListService listService;


    //添加
    @RequestMapping(value = "put",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody Product product,BindingResult result){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("--商户商品信息：{}",product);
            response.setData(listService.addProduct(product));
        }catch (Exception e){
            log.error("--List实战-商户商品-添加-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //添加
    @RequestMapping(value = "put/v2",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putV2(@RequestBody @Validated Product product,BindingResult result){
        String checkRes=ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.InvalidParams.getCode(),checkRes);
        }

        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("--商户商品信息v2：{}",product);

        }catch (Exception e){
            log.error("--List实战-商户商品-添加-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    //获取列表详情
    @RequestMapping(value = "get",method = RequestMethod.GET)
    public BaseResponse get(@RequestParam("userId") final Integer userId){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(listService.getHistoryProducts(userId));

        }catch (Exception e){
            log.error("--List实战-商户商品-获取列表-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //平台发送通知给到各位商户
    @RequestMapping(value = "/notice/put",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putNotice(@RequestBody @Validated Notice notice, BindingResult result){
        String checkRes=ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.Fail.getCode(),checkRes);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("--平台发送通知给到各位商户：{}",notice);

            listService.pushNotice(notice);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}

















































