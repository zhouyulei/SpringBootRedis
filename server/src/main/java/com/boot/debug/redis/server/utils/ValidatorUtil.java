package com.boot.debug.redis.server.utils;/**
 * Created by Administrator on 2019/10/30.
 */

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.function.Consumer;

/**
 * 统一校验前端参数的工具
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 10:46
 **/
public class ValidatorUtil {

    //TODO:统一校验前端传递过来的参数
    public static String checkResult(BindingResult result){
        StringBuilder sb=new StringBuilder("");
        if (result.hasErrors()){
            /*List<ObjectError> list=result.getAllErrors();
            for (ObjectError error:list){
                sb.append(error.getDefaultMessage()).append("\n");
            }*/

            //java8 steam api
            result.getAllErrors().forEach(error -> sb.append(error.getDefaultMessage()).append("\n"));
        }
        return sb.toString();
    }

}

































