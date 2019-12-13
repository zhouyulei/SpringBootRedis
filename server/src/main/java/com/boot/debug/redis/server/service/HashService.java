package com.boot.debug.redis.server.service;

import com.boot.debug.redis.model.entity.SysConfig;
import com.boot.debug.redis.model.mapper.SysConfigMapper;
import com.boot.debug.redis.server.redis.HashRedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**hash数据类型-service
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/31 21:07
 **/
@Service
public class HashService {

    private static final Logger log= LoggerFactory.getLogger(HashService.class);

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private HashRedisService hashRedisService;

    //TODO:添加数据字典及其对应的选项(code-value)
    @Transactional(rollbackFor = Exception.class)
    public Integer addSysConfig(SysConfig config) throws Exception{
        int res=sysConfigMapper.insertSelective(config);
        if (res>0){
            //TODO:实时触发数据字典的hash存储
            hashRedisService.cacheConfigMap();
        }
        return config.getId();
    }

    //TODO:取出缓存中所有的数据字典列表
    public Map<String,List<SysConfig>> getAll() throws Exception{
        return hashRedisService.getAllCacheConfig();
    }

    //TODO:取出缓存中特定的数据字典列表
    public List<SysConfig> getByType(final String type) throws Exception{
        return hashRedisService.getCacheConfigByType(type);
    }
}




























