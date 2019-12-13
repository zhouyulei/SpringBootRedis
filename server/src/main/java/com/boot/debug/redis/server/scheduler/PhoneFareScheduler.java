package com.boot.debug.redis.server.scheduler;

import com.boot.debug.redis.model.dto.FareDto;
import com.boot.debug.redis.model.entity.PhoneFare;
import com.boot.debug.redis.model.mapper.PhoneFareMapper;
import com.boot.debug.redis.server.enums.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**补偿性手机号码充值排行榜
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/31 16:22
 **/
@Component
public class PhoneFareScheduler {

    private static final Logger log= LoggerFactory.getLogger(PhoneFareScheduler.class);

    @Autowired
    private PhoneFareMapper phoneFareMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    //@Scheduled(cron = "0/20 * * * * ?")
    public void sortFareScheduler(){
        log.info("--补偿性手机号码充值排行榜-定时任务");

        this.cacheSortResult();
    }

    //@Async("threadPoolTaskExecutor")
    private void cacheSortResult(){
        try {
            ZSetOperations<String,FareDto> zSetOperations=redisTemplate.opsForZSet();

            List<PhoneFare> list=phoneFareMapper.getAllSortFares();
            if (list!=null && !list.isEmpty()){
                redisTemplate.delete(Constant.RedisSortedSetKey2);

                list.forEach(fare -> {
                    FareDto dto=new FareDto(fare.getPhone());
                    zSetOperations.add(Constant.RedisSortedSetKey2,dto,fare.getFare().doubleValue());
                });
            }
        }catch (Exception e){
            log.error("--补偿性手机号码充值排行榜-定时任务-发生异常：",e.fillInStackTrace());
        }
    }
}

































