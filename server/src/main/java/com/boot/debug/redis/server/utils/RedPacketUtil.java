package com.boot.debug.redis.server.utils;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

/**
 * 红包随机金额生成算法-二倍均值法
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2019/11/2 20:56
 **/
public class RedPacketUtil {

    /**
     * 二倍均值法的算法实现 - 算法里面的金额以 分 为单位
     * @param totalAmount
     * @param totalPeople
     * @return
     */
    public static List<Integer> divideRedPacket(final Integer totalAmount,final Integer totalPeople){
        List<Integer> list= Lists.newLinkedList();

        if (totalAmount>0 && totalPeople>0){
            Integer restAmount=totalAmount;
            Integer restPeople=totalPeople;

            Random random=new Random();
            int amount;
            for (int i=0;i<totalPeople-1;i++){
                //左闭右开 [1,剩余金额/剩余人数 的除数 的两倍 )

                amount=random.nextInt(restAmount/restPeople * 2 - 1)  + 1;
                list.add(amount);

                //剩余金额、剩余人数
                restAmount -= amount;
                restPeople--;
            }

            //最后一个剩余的金额
            list.add(restAmount);
        }
        return list;
    }

//    public static void main(String[] args) {
//        Integer amount=100;
//        Integer people=10;
//
//        List<Integer> list=divideRedPacket(amount,people);
//        System.out.println("--"+list);
//
//        Integer total=0;
//        for (Integer a:list){
//            total+=a;
//        }
//        System.out.println("--"+total);
//    }

}










































