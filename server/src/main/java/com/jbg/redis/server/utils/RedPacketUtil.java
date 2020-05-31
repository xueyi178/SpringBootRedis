package com.jbg.redis.server.utils;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;

/**
 * <p>
 *     抢红包系统中的 二倍均值法
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 15:34
 */
public class RedPacketUtil {
    /**
     * 1. 描述: 二倍均值法的算法实现 - 算法里面的金额以 分 为单位
     *    作者: xueyi
     *    日期: 2020/5/31 15:36
     *    参数: [totalAmount: 红包的总金额, totalPeople: 红包的个数]
     *    返回: java.util.List<java.lang.Integer>
     */
    public static List<Integer> divideRedPacket(final Integer totalAmount, final Integer totalPeople){
        List<Integer> list= Lists.newLinkedList();

        if (totalAmount>0 && totalPeople>0){
            // 红包的总金额
            Integer restAmount=totalAmount;
            // 红包的个数
            Integer restPeople=totalPeople;
            Random random=new Random();
            int amount;
            for (int i=0;i < totalPeople - 1;i++){
                //左闭右开 [1,剩余金额/剩余人数 的除数 的两倍 )
                //不加1, 可能会出现0的情况
                amount=random.nextInt(restAmount/restPeople * 2 - 1)  + 1;
                list.add(amount);
                //剩余金额
                restAmount -= amount;
                //剩余人数
                restPeople--;
            }
            //最后一个剩余的金额
            list.add(restAmount);
        }
        return list;
    }

    public static void main(String[] args) {
        Integer amount=100;
        Integer people=10;

        List<Integer> list=divideRedPacket(amount,people);
        System.out.println("--"+list);

        Integer total=0;
        for (Integer a:list){
            total+=a;
        }
        System.out.println("--"+total);
    }

}
