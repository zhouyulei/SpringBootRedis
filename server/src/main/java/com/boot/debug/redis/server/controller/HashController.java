package com.boot.debug.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.SysConfig;
import com.boot.debug.redis.server.service.HashService;
import com.boot.debug.redis.server.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数据类型Hash散列-减少key存储、类似于map-可以通过键取得其 “值” (可以对象列表...)
 * @Author:debug (SteadyJack)
 * @Date: 2019/10/31 21:05
 **/
@RestController
@RequestMapping("hash")
public class HashController extends AbstractController {

    @Autowired
    private HashService hashService;

    @RequestMapping(value = "put",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated SysConfig config, BindingResult result){
        String checkRes= ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.Fail.getCode(),checkRes);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            hashService.addSysConfig(config);

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "get",method = RequestMethod.GET)
    public BaseResponse get(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(hashService.getAll());

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "get/type",method = RequestMethod.GET)
    public BaseResponse getType(@RequestParam String type){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(hashService.getByType(type));

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}





























