package com.boot.debug.redis.server.service;

import com.boot.debug.redis.api.response.StatusCode;
import com.boot.debug.redis.model.entity.Problem;
import com.boot.debug.redis.model.entity.User;
import com.boot.debug.redis.model.mapper.UserMapper;
import com.boot.debug.redis.server.enums.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 集合set服务
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/30 21:16
 **/
@Service
public class SetService {

    private static final Logger log= LoggerFactory.getLogger(SetService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProblemService problemService;


    //TODO:用户注册
    @Transactional(rollbackFor = Exception.class)
    public Integer registerUser(User user) throws Exception{
        if (this.exist(user.getEmail())){
            throw new RuntimeException(StatusCode.UserEmailHasExist.getMsg());
        }

        int res=userMapper.insertSelective(user);
        if (res>0){
            SetOperations<String,String> setOperations=redisTemplate.opsForSet();
            setOperations.add(Constant.RedisSetKey,user.getEmail());
        }
        return user.getId();
    }


    //TODO:判断邮箱是否已存在于缓存中
    private Boolean exist(final String email) throws Exception{
        //TODO:写法一
        /*SetOperations<String,String> setOperations=redisTemplate.opsForSet();
        Boolean res=setOperations.isMember(Constant.RedisSetKey,email);
        if (res){
            return true;
        }else{
            User user=userMapper.selectByEmail(email);
            if (user!=null){
                setOperations.add(Constant.RedisSetKey,user.getEmail());
                return true;
            }else{
                return false;
            }
        }*/

        //TODO:写法二
        SetOperations<String,String> setOperations=redisTemplate.opsForSet();
        Long size=setOperations.size(Constant.RedisSetKey);
        if (size>0 &&  setOperations.isMember(Constant.RedisSetKey,email)){
            return true;
        }else{
            User user=userMapper.selectByEmail(email);
            if (user!=null){
                setOperations.add(Constant.RedisSetKey,user.getEmail());
                return true;
            }else{
                return false;
            }
        }
    }

    //TODO:取出缓存中已注册的用户的邮箱列表
    public Set<String> getEmails() throws Exception{
        /*SetOperations<String,String> setOperations=redisTemplate.opsForSet();
        return setOperations.members(Constant.RedisSetKey);*/
        return redisTemplate.opsForSet().members(Constant.RedisSetKey);

        //return setOperations.randomMembers(Constant.RedisSetKey,setOperations.size(Constant.RedisSetKey));
    }



    //TODO:从问题库中弹出一个随机的问题
    public Problem getRandomProblem() throws Exception{
        return problemService.getRandomEntity();
    }

    //TODO:从问题库中取出固定数量的随机的、乱序试题列表
    public Set<Problem> getRandomProblems(Integer total) throws Exception{
        return problemService.getRandomEntitys(total);
    }
}

































