package com.boot.debug.redis.server.controller;

import com.boot.debug.redis.api.response.BaseResponse;
import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.server.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Cacheable、@CachePut、@CacheEvict等注解
 * 商品详情controller
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/11/3 15:07
 **/
@RestController
@RequestMapping("item")
public class ItemController extends AbstractController{

    @Autowired
    private ItemService itemService;

    //获取详情-@Cacheable
    @RequestMapping(value = "info",method = RequestMethod.GET)
    public BaseResponse info(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(itemService.getInfo(id));

        }catch (Exception e){
            log.error("--商品详情controller-详情-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //获取详情
    @RequestMapping(value = "info/v2",method = RequestMethod.GET)
    public BaseResponse infoV2(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(itemService.getInfoV2(id));

        }catch (Exception e){
            log.error("--商品详情controller-详情-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    //删除
    @RequestMapping(value = "delete",method = RequestMethod.GET)
    public BaseResponse delete(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            itemService.delete(id);

        }catch (Exception e){
            log.error("--商品详情controller-删除-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}































