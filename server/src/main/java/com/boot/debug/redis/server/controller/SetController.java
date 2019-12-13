package com.boot.debug.redis.server.controller;

import cn.hutool.core.util.StrUtil;
import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.User;
import com.boot.debug.redis.server.service.SetService;
import com.boot.debug.redis.server.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数据类型为Set - 数据元素不重复(过滤掉重复的元素；判断一个元素是否存在于一个大集合中)
 * @Author:debug (SteadyJack)
 * @Date: 2019/10/30 21:16
 **/
@RestController
@RequestMapping("set")
public class SetController extends AbstractController {

    @Autowired
    private SetService setService;

    //TODO:提交用户注册
    @RequestMapping(value = "put",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse put(@RequestBody @Validated User user, BindingResult result){
        String checkRes=ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)){
            return new BaseResponse(StatusCode.Fail.getCode(),checkRes);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("----用户注册信息：{}",user);

            response.setData(setService.registerUser(user));
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //TODO:取出缓存集合Set中所有已注册的用户的邮箱列表
    @RequestMapping(value = "get",method = RequestMethod.GET)
    public BaseResponse get(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(setService.getEmails());
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //TODO:取出随机问题库中弹出随机的问题
    @RequestMapping(value = "problem/random",method = RequestMethod.GET)
    public BaseResponse getRandomProblem(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(setService.getRandomProblem());
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    //TODO:取出(不移除)随机问题库-固定数量的随机试卷题目
    @RequestMapping(value = "problems/random",method = RequestMethod.GET)
    public BaseResponse getRandomProblems(@RequestParam Integer total){
        if (total<=0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(setService.getRandomProblems(total));

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}






















