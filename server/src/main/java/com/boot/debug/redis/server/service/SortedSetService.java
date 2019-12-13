package com.boot.debug.redis.server.service;

import com.boot.debug.redis.model.dto.FareDto;
import com.boot.debug.redis.model.entity.PhoneFare;
import com.boot.debug.redis.model.mapper.PhoneFareMapper;
import com.boot.debug.redis.server.enums.Constant;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 有序集合service
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/31 15:10
 **/
@Service
public class SortedSetService {

    private static final Logger log= LoggerFactory.getLogger(SortedSetService.class);

    @Autowired
    private PhoneFareMapper fareMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //TODO:新增/手机话费充值 记录
    @Transactional(rollbackFor = Exception.class)
    public Integer addRecord(PhoneFare fare) throws Exception{
        log.info("----sorted set话费充值记录新增：{} ",fare);

        int res=fareMapper.insertSelective(fare);
        if (res>0){
            ZSetOperations<String,PhoneFare> zSetOperations=redisTemplate.opsForZSet();
            zSetOperations.add(Constant.RedisSortedSetKey1,fare,fare.getFare().doubleValue());
        }
        return fare.getId();
    }

    //TODO:获取充值排行榜
    public Set<PhoneFare> getSortFares(final Boolean isAsc){
        final String key=Constant.RedisSortedSetKey1;
        ZSetOperations<String,PhoneFare> zSetOperations=redisTemplate.opsForZSet();
        final Long size=zSetOperations.size(key);

        return isAsc ? zSetOperations.range(key,0L,size) : zSetOperations.reverseRange(key,0L,size);
    }




    //TODO:新增/手机话费充值 记录 v2
    @Transactional(rollbackFor = Exception.class)
    public Integer addRecordV2(PhoneFare fare) throws Exception{
        log.info("----sorted set话费充值记录新增V2：{} ",fare);

        int res=fareMapper.insertSelective(fare);
        if (res>0){
            FareDto dto=new FareDto(fare.getPhone());

            ZSetOperations<String,FareDto> zSetOperations=redisTemplate.opsForZSet();
            Double oldFare=zSetOperations.score(Constant.RedisSortedSetKey2,dto);
            if (oldFare!=null){
                //TODO:表示之前该手机号对应的用户充过值了，需要进行叠加
                zSetOperations.incrementScore(Constant.RedisSortedSetKey2,dto,fare.getFare().doubleValue());
            }else{
                //TODO:表示只充过一次话费
                zSetOperations.add(Constant.RedisSortedSetKey2,dto,fare.getFare().doubleValue());
            }

        }
        return fare.getId();
    }

    //TODO:获取充值排行榜V2
    public List<PhoneFare> getSortFaresV2(){
        List<PhoneFare> list= Lists.newLinkedList();

        final String key=Constant.RedisSortedSetKey2;
        ZSetOperations<String,FareDto> zSetOperations=redisTemplate.opsForZSet();
        final Long size=zSetOperations.size(key);

        Set<ZSetOperations.TypedTuple<FareDto>> set=zSetOperations.reverseRangeWithScores(key,0L,size);
        if (set!=null && !set.isEmpty()){
            set.forEach(tuple -> {
                PhoneFare fare=new PhoneFare();
                fare.setFare(BigDecimal.valueOf(tuple.getScore()));
                fare.setPhone(tuple.getValue().getPhone());

                list.add(fare);
            });
        }
        return list;
    }
}































