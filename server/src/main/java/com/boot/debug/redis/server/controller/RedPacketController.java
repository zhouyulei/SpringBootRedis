package com.boot.debug.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.dto.RedPacketDto;
import com.boot.debug.redis.server.service.RedPacketService;
import com.boot.debug.redis.server.utils.ValidatorUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 抢红包系统controller
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/11/2 17:15
 **/
@RestController
@RequestMapping("red/packet")
public class RedPacketController extends AbstractController{

    @Autowired
    private RedPacketService redPacketService;



    //发
    @RequestMapping(value = "hand/out",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse handOut(@Validated @RequestBody RedPacketDto dto, BindingResult result){
        String checkRes=ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.InvalidParams.getCode(),checkRes);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        Map<String,Object> resMap= Maps.newHashMap();
        try {
            resMap.put("redKey",redPacketService.handOut(dto));

        }catch (Exception e){
            log.error("发红包业务模块发生异常：dto={} ",dto,e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        response.setData(resMap);
        return response;
    }


    //抢
    @RequestMapping(value = "rob",method = RequestMethod.GET)
    public BaseResponse rob(@RequestParam Integer userId, @RequestParam String redKey){
        if (userId<=0 || StrUtil.isBlank(redKey)){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            //Integer redValue=redPacketService.rob(userId,redKey);
            //Integer redValue=redPacketService.robV2(userId,redKey);
            Integer redValue=redPacketService.robV3(userId,redKey);
            if (redValue!=null){
                response.setData(redValue);
            }else{
                return new BaseResponse(StatusCode.Fail.getCode(),"红包已被抢完！");
            }

        }catch (Exception e){
            log.error("抢红包业务模块发生异常：userId={} redKey={}",userId,redKey,e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

}





























