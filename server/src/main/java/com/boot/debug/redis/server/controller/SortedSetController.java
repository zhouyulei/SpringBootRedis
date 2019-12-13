package com.boot.debug.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.PhoneFare;
import com.boot.debug.redis.server.enums.Constant;
import com.boot.debug.redis.server.service.SortedSetService;
import com.boot.debug.redis.server.utils.ValidatorUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据类型为SortedSet - list + set的结合体 - 可用于实现不重复的、有序的元素存储
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/31 15:10
 **/
@RestController
@RequestMapping("sorted/set")
public class SortedSetController extends AbstractController {

    @Autowired
    private SortedSetService sortedSetService;

    @RequestMapping(value = "put",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated PhoneFare fare, BindingResult result){
        String checkRes= ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.Fail.getCode(),checkRes);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(sortedSetService.addRecord(fare));

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "get",method = RequestMethod.GET)
    public BaseResponse get(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        Map<String,Object> resMap=Maps.newHashMap();
        try {
            resMap.put("SortFaresASC",sortedSetService.getSortFares(true));
            resMap.put("SortFaresDESC",sortedSetService.getSortFares(false));

        }catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        response.setData(resMap);
        return response;
    }








    @RequestMapping(value = "put/v2",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse putv2(@RequestBody @Validated PhoneFare fare, BindingResult result){
        String checkRes= ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.Fail.getCode(),checkRes);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(sortedSetService.addRecordV2(fare));

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "get/v2",method = RequestMethod.GET)
    public BaseResponse getV2(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(sortedSetService.getSortFaresV2());
        }catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}






















