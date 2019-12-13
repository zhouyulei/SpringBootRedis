package com.boot.debug.redis.test;/**
 * Created by Administrator on 2019/10/29.
 */

import com.boot.debug.redis.server.MainApplication;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * java单元测试
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/10/29 20:41
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class RedisTestOne {

    private static final Logger log = LoggerFactory.getLogger(RedisTestOne.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void method1() {
        log.info("----开始字符串测试");

        final String key = "SpringBootRedis:Order:10012";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, 10012L);

        log.info("---当前key={} 对应的value={}", key, valueOperations.get(key));

        valueOperations.increment(key, 10012L);
        log.info("---当前key={} 对应的value={}", key, valueOperations.get(key));


    }


    @Test
    public void method2() {
        log.info("----开始列表List测试");

        final String key = "SpringBootRedis:List:10010";
        redisTemplate.delete(key);

        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        //TODO: e,d,c,b,a
        List<String> list = Lists.newArrayList("c", "d", "e");
        listOperations.leftPush(key, "a");
        listOperations.leftPush(key, "b");
        listOperations.leftPushAll(key, list);

        log.info("--当前列表元素个数：{} ", listOperations.size(key));
        log.info("--当前列表元素：{} ", listOperations.range(key, 0, 10));

        log.info("--当前列表中下标为0的元素：{} ", listOperations.index(key, 0L));
        log.info("--当前列表中下标为4的元素：{} ", listOperations.index(key, 4L));
        log.info("--当前列表中下标为10的元素：{} ", listOperations.index(key, 10L));

        log.info("--当前列表从右边弹出来：{} ", listOperations.rightPop(key));

        listOperations.set(key, 0L, "100");
        log.info("--当前列表中下标为0的元素：{} ", listOperations.index(key, 0L));
        log.info("--当前列表元素：{} ", listOperations.range(key, 0, 10));

        listOperations.remove(key, 0L, "100");
        log.info("--当前列表元素：{} ", listOperations.range(key, 0, 10));
    }


    @Test
    public void method3() {
        log.info("----开始集合Set测试");

        final String key1 = "SpringBootRedis:Set:10010";
        final String key2 = "SpringBootRedis:Set:10011";
        redisTemplate.delete(key1);
        redisTemplate.delete(key2);

        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        setOperations.add(key1, new String[]{"a", "b", "c"});
        setOperations.add(key2, new String[]{"b", "e", "f"});

        log.info("---集合key1的元素：{}", setOperations.members(key1));
        log.info("---集合key2的元素：{}", setOperations.members(key2));

        log.info("---集合key1随机取1个元素：{}", setOperations.randomMember(key1));
        log.info("---集合key1随机取n个元素：{}", setOperations.randomMembers(key1, 2L));

        log.info("---集合key1元素个数：{}", setOperations.size(key1));
        log.info("---集合key2元素个数：{}", setOperations.size(key2));

        log.info("---元素a是否为集合key1的元素：{}", setOperations.isMember(key1, "a"));
        log.info("---元素f是否为集合key1的元素：{}", setOperations.isMember(key1, "f"));

        log.info("---集合key1和集合key2的差集元素：{}", setOperations.difference(key1, key2));
        log.info("---集合key1和集合key2的交集元素：{}", setOperations.intersect(key1, key2));
        log.info("---集合key1和集合key2的并集元素：{}", setOperations.union(key1, key2));

        log.info("---从集合key1中弹出一个随机的元素：{}", setOperations.pop(key1));
        log.info("---集合key1的元素：{}", setOperations.members(key1));
        log.info("---将c从集合key1的元素列表中移除：{}", setOperations.remove(key1, "c"));

    }


    @Test
    public void method4() {
        log.info("----开始有序集合SortedSet测试");

        final String key = "SpringBootRedis:SortedSet:10010";
        redisTemplate.delete(key);

        ZSetOperations<String,String> zSetOperations=redisTemplate.opsForZSet();

        zSetOperations.add(key,"a",8.0);
        zSetOperations.add(key,"b",2.0);
        zSetOperations.add(key,"c",4.0);
        zSetOperations.add(key,"d",6.0);

        log.info("---有序集合SortedSet-成员数：{}",zSetOperations.size(key));
        log.info("---有序集合SortedSet-按照分数正序：{}",zSetOperations.range(key,0L,zSetOperations.size(key)));
        log.info("---有序集合SortedSet-按照分数倒序：{}",zSetOperations.reverseRange(key,0L,zSetOperations.size(key)));
        log.info("---有序集合SortedSet-获取成员a的得分：{}",zSetOperations.score(key,"a"));
        log.info("---有序集合SortedSet-获取成员c的得分：{}",zSetOperations.score(key,"c"));

        log.info("---有序集合SortedSet-正序中c的排名：{} 名",zSetOperations.rank(key,"c")+1);
        log.info("---有序集合SortedSet-倒序中c的排名：{} 名",zSetOperations.reverseRank(key,"c"));

        zSetOperations.incrementScore(key,"b",10.0);
        log.info("---有序集合SortedSet-按照分数倒序：{}",zSetOperations.reverseRange(key,0L,zSetOperations.size(key)));

        zSetOperations.remove(key,"b");
        log.info("---有序集合SortedSet-按照分数倒序：{}",zSetOperations.reverseRange(key,0L,zSetOperations.size(key)));

        log.info("---有序集合SortedSet-取出分数区间的成员：{}",zSetOperations.rangeByScore(key,0,7));

        log.info("---有序集合SortedSet-取出带分数的排好序的成员：");
        Set<ZSetOperations.TypedTuple<String>> set=zSetOperations.rangeWithScores(key,0L,zSetOperations.size(key));
        set.forEach(tuple -> log.info("--当前成员：{} 对应的分数：{}",tuple.getValue(),tuple.getScore()));

    }


    @Test
    public void method5() {
        log.info("----开始哈希Hash测试");

        final String key = "SpringBootRedis:Hash:Key:v1";
        redisTemplate.delete(key);

        HashOperations<String,String,String> hashOperations=redisTemplate.opsForHash();
        hashOperations.put(key,"10010","zhangsan");
        hashOperations.put(key,"10011","lisi");

        Map<String,String> dataMap= Maps.newHashMap();
        dataMap.put("10012","wangwu");
        dataMap.put("10013","zhaoliu");
        hashOperations.putAll(key,dataMap);

        log.info("---哈希hash-获取列表元素： {} ",hashOperations.entries(key));
        log.info("---哈希hash-获取10012的元素： {} ",hashOperations.get(key,"10012"));
        log.info("---哈希hash-获取所有元素的field列表： {} ",hashOperations.keys(key));

        log.info("---哈希hash-10013成员是否存在： {} ",hashOperations.hasKey(key,"10013"));
        log.info("---哈希hash-10014成员是否存在： {} ",hashOperations.hasKey(key,"10014"));

        hashOperations.putIfAbsent(key,"10020","sunwukong");
        log.info("---哈希hash-获取列表元素： {} ",hashOperations.entries(key));

        log.info("---哈希hash-删除元素10010 10011： {} ",hashOperations.delete(key,"10010","10011"));
        log.info("---哈希hash-获取列表元素： {} ",hashOperations.entries(key));

        log.info("---哈希hash-获取列表元素个数： {} ",hashOperations.size(key));

        /*redisTemplate.opsForHyperLogLog();
        redisTemplate.opsForGeo();*/
    }


    @Test
    public void method6() throws Exception{
        log.info("----开始key失效测试");

        final String key = "SpringBootRedis:Hash:Key:Expire";
        redisTemplate.delete(key);

        ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
        valueOperations.set(key,"lixiaolong",5L, TimeUnit.SECONDS);

        log.info("---取出数据：{}",valueOperations.get(key));
        Thread.sleep(5000);
        log.info("---取出数据：{}",valueOperations.get(key));

        valueOperations.set(key,"bruceLee");
        redisTemplate.expire(key,5L,TimeUnit.SECONDS);
        log.info("---取出数据：{}",valueOperations.get(key));
        Thread.sleep(5000);
        log.info("---取出数据：{}",valueOperations.get(key));

    }

    @Test
    public void method7() {
        log.info("----开始基于注解的缓存实战测试");

        log.info("--{}",stringRedisTemplate.opsForValue().get("SpringBootRedis:Item::1"));
    }

    @Test
    public void test8(){
        

    }
}






































