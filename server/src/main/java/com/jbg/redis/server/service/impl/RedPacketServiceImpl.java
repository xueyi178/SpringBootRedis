package com.jbg.redis.server.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.jbg.redis.model.entity.RedDetail;
import com.jbg.redis.model.entity.RedRecord;
import com.jbg.redis.model.entity.RedRobRecord;
import com.jbg.redis.model.mapper.RedDetailMapper;
import com.jbg.redis.model.mapper.RedRecordMapper;
import com.jbg.redis.model.mapper.RedRobRecordMapper;
import com.jbg.redis.server.utils.RedPacketUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *      抢红包系统 Service 业务逻辑
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 15:26
 */
@Slf4j
@Service
public class RedPacketServiceImpl {
    private static final String RedPacketKey="SpringBootRedis:RedPacket:%s:%s";

    private static final Snowflake SNOWFLAKE=new Snowflake(3,2);

    @Autowired
    private RedRecordMapper redRecordMapper;

    @Autowired
    private RedDetailMapper redDetailMapper;

    @Autowired
    private RedRobRecordMapper redRobRecordMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 1. 描述: 发红包的业务逻辑
     *    作者: xueyi
     *    日期: 2020/5/31 15:49
     *    参数: [dto]
     *    返回: java.lang.String
     */
    @Transactional(rollbackFor = Exception.class)
    public String handOut(RedRecord dto) {
        if (dto.getTotal()>0 && dto.getAmount().intValue() >0){
            //TODO:生成红包全局唯一标识串
            String redKey=String.format(RedPacketKey,dto.getUserId(),SNOWFLAKE.nextIdStr());

            //TODO:生成红包随机金额列表
            List<Integer> redList= RedPacketUtil.divideRedPacket(dto.getAmount().intValue(),dto.getTotal());

            //TODO:入库
            recordRedPacket(dto,redList,redKey);

            //TODO:入缓存=总个数、随机金额列表
            redisTemplate.opsForValue().set(redKey+":total",dto.getTotal());
            //TODO: 保存到Redis中的List
            redisTemplate.opsForList().leftPushAll(redKey,redList);

            return redKey;
        }
        return null;
    }

    //TODO:发红包-入库
    /**
     *    描述: 发红包-入库
     *    作者: xueyi
     *    日期: 2020/5/31 15:56
     *    参数: [dto: 发红包的记录, list: 红包的详情, redKey: 红包的key]
     *    返回: void
     */
    public void recordRedPacket(RedRecord dto,List<Integer> list,final String redKey){
        //发红包的记录
        RedRecord entity=new RedRecord();
        entity.setCreateTime(new Date());
        entity.setUserId(dto.getUserId());
        entity.setRedPacket(redKey);
        entity.setTotal(dto.getTotal());
        entity.setAmount(BigDecimal.valueOf(dto.getAmount().intValue()));
        redRecordMapper.insertSelective(entity);

        //红包的详情
        list.parallelStream().forEach(redAmount -> {
            RedDetail detail=new RedDetail();
            detail.setRecordId(entity.getId());
            detail.setAmount(BigDecimal.valueOf(redAmount));
            redDetailMapper.insertSelective(detail);
        });
    }

    /**
     * 3. 描述: 红包-点 、拆/开 红包操作组成  - 金额为分
     *    作者: xueyi
     *    日期: 2020/5/31 16:25
     *    参数: [userId, redKey]
     *    返回: java.lang.Integer
     */
    //TODO:抢红包-点 、拆/开 红包操作组成  - 金额为分
    public Integer rob(final Integer userId,final String redKey) throws Exception{

        //TODO:点 逻辑
        Boolean existRed=clickRed(redKey);
        if (existRed){
            //TODO:拆/开 逻辑

            //TODO:弹出一个随机金额,从右弹出一个红包金额
            Object value=redisTemplate.opsForList().rightPop(redKey);
            if (value!=null){
                //TODO:把缓存中的红包个数减一
                final String redTotalKey=redKey+":total";
                ValueOperations valueOperations = redisTemplate.opsForValue();
                //TODO:获取红包的总个数
                Object currTotalObj = valueOperations.get(redTotalKey);
                //TODO: 判断红包的个数是否为空
                Integer currTotal= ObjectUtil.isNotEmpty(currTotalObj) ? Integer.valueOf(String.valueOf(currTotalObj)) : 0;
                //TODO: 把红包的个数减一
                valueOperations.set(redTotalKey,currTotal-1);

                //TODO:添加到抢红包的入库记录
                final Integer realRedValue=Integer.valueOf(String.valueOf(value));
                recordRobRedPacket(userId,redKey,realRedValue);

                log.info("---当前用户抢到了红包：redKey={} userId={} redValue={} ",redKey,userId,realRedValue);
                return realRedValue;
            }
        }
        return null;
    }

    /**
     * 4. 描述: 红包-点 、拆/开 红包操作组成  - 金额为分
     *    作者: xueyi
     *    日期: 2020/5/31 16:25
     *    参数: [userId, redKey]
     *    返回: java.lang.Integer
     */
    //TODO:抢红包-点 、拆/开 红包操作组成  - 金额为分
    public Integer robV2(final Integer userId,final String redKey) throws Exception{

        ValueOperations valueOperations=redisTemplate.opsForValue();

        //TODO：判断当前用户是否抢过了红包,从redis中获取
        final String redUserKey = redKey+userId+":rob";
        Object cacheRedValue = valueOperations.get(redUserKey);
        if (cacheRedValue != null){
            return Integer.valueOf(String.valueOf(cacheRedValue));
        }

        //TODO:点 逻辑
        Boolean existRed=clickRed(redKey);
        if (existRed){
            //TODO:拆/开 逻辑

            //TODO:弹出一个随机金额
            Object value=redisTemplate.opsForList().rightPop(redKey);
            if (value!=null){

                //TODO:红包个数减一
                final String redTotalKey=redKey+":total";
                valueOperations.increment(redTotalKey,-1L);

                //TODO:入库
                final Integer realRedValue=Integer.valueOf(String.valueOf(value));
                recordRobRedPacket(userId,redKey,realRedValue);

                //TODO:将当前用户抢到的红包金额塞入缓存
                valueOperations.set(redUserKey,value,24L, TimeUnit.HOURS);

                log.info("---当前用户抢到了红包：redKey={} userId={} redValue={} ",redKey,userId,realRedValue);
                return realRedValue;
            }
        }
        return null;
    }

    /**
     * 5. 描述: 红包-点 、拆/开 红包操作组成  - 金额为分
     *    作者: xueyi
     *    日期: 2020/5/31 16:25
     *    参数: [userId, redKey]
     *    返回: java.lang.Integer
     */
    //TODO:抢红包-点 、拆/开 红包操作组成  - 金额为分
    public Integer robV3(final Integer userId,final String redKey) throws Exception{
        ValueOperations valueOperations=redisTemplate.opsForValue();

        //TODO：判断当前用户是否抢过了红包
        final String redUserKey=redKey+userId+":rob";
        Object cacheRedValue=valueOperations.get(redUserKey);
        if (cacheRedValue!=null){
            return Integer.valueOf(String.valueOf(cacheRedValue));
        }

        //TODO:点 逻辑
        Boolean existRed=clickRed(redKey);
        if (existRed){
            //TODO:加锁-分布式锁-redis实现-：一个小的随机红包 每个人只能抢一次；一个人每次只能抢到一个小的随机红包金额 - 永远保证1:1的关系
            final String lockKey=redKey+userId+"-lock";
            Boolean lock=valueOperations.setIfAbsent(lockKey,redKey);
            try {
                if (lock){
                    redisTemplate.expire(lockKey,48L, TimeUnit.HOURS);

                    //TODO:拆/开 逻辑

                    //TODO:弹出一个随机金额
                    Object value=redisTemplate.opsForList().rightPop(redKey);
                    if (value!=null){

                        //TODO:红包个数减一
                        final String redTotalKey=redKey+":total";
                        valueOperations.increment(redTotalKey,-1L);

                        //TODO:入库
                        final Integer realRedValue=Integer.valueOf(String.valueOf(value));
                        recordRobRedPacket(userId,redKey,realRedValue);

                        //TODO:将当前用户抢到的红包金额塞入缓存
                        valueOperations.set(redUserKey,value,24L, TimeUnit.HOURS);

                        log.info("---加分布式锁-当前用户抢到了红包：redKey={} userId={} redValue={} ",redKey,userId,realRedValue);
                        return realRedValue;
                    }

                }
            }finally {
                //TODO:需要释放锁吗？不需要释放锁 - 那是因为红包一发出去，就是一个新的key;一旦被抢完，生命周期永远终止
                //TODO: 释放锁就是为了高并发,
            }

        }
        return null;
    }

    //TODO:判断缓存系统中红包的个数
    private Boolean clickRed(final String redKey) throws Exception{
        Object total=redisTemplate.opsForValue().get(redKey+":total");
        if (total!=null && Integer.valueOf(String.valueOf(total))>0){
            return true;
        }
        return false;
    }

    //TODO:记录抢到的红包明细
    private void recordRobRedPacket(final Integer userId,final String redKey,final Integer amount) throws Exception{
        RedRobRecord entity=new RedRobRecord();
        entity.setUserId(userId);
        entity.setRedPacket(redKey);
        entity.setAmount(BigDecimal.valueOf(amount));
        entity.setRobTime(DateTime.now().toDate());
        redRobRecordMapper.insertSelective(entity);
    }

}
