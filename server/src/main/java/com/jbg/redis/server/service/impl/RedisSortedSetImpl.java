package com.jbg.redis.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.jbg.redis.model.Dto.PhoneFareDto;
import com.jbg.redis.model.entity.PhoneFare;
import com.jbg.redis.model.mapper.PhoneFareMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
 * <p>
 *      Redis中Sorted数据类型的存储 Service 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/5/16 14:56
 */
@Service
@Slf4j
public class RedisSortedSetImpl {

    @Autowired
    private PhoneFareMapper phoneFareMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 1. 描述: 新增手机充值记录
     *    作者: xueyi
     *    日期: 2020/5/16 15:00
     *    参数: []
     *    返回: java.lang.Integer
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer addRecord(PhoneFare phoneFare){
        log.info("-----SortedSet话费充值记录-----", phoneFare);
        int i = phoneFareMapper.insertSelective(phoneFare);
        if (i > 0) {
            ZSetOperations<String, PhoneFare> phoneFareZSetOperations = redisTemplate.opsForZSet();
            Boolean falg = phoneFareZSetOperations.add("SpringBootRedis:Sorted:PhoneFare:v1", phoneFare, phoneFare.getFare().doubleValue());
            log.info("新增手机充值记录到SortedSet中:[]",falg);
        }
        return i > 0 ? phoneFare.getId() : 0;
    }

    /**
     * 1. 描述: 新增手机充值记录v2
     *    作者: xueyi
     *    日期: 2020/5/16 15:00
     *    参数: []
     *    返回: java.lang.Integer
     *    每次用户充的数值fare取值是不一样的 ~ 导致了虽然手机号一样，但是整体的实体对象已经是新的了
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer addRecordV2(PhoneFare phoneFare){
        final String key = "SpringBootRedis:Sorted:PhoneFare:v2";

        log.info("-----SortedSet话费充值记录-----", phoneFare);
        int i = phoneFareMapper.insertSelective(phoneFare);
        if (i > 0) {
            PhoneFareDto fareDto = new PhoneFareDto(phoneFare.getPhone());
            ZSetOperations<String, PhoneFareDto> phoneFareZSetOperations = redisTemplate.opsForZSet();
            // 根据键获取缓存中的数据
            Double score = phoneFareZSetOperations.score(key, fareDto);
            if (!ObjectUtil.isEmpty(score)) {
                // 表示该用户的手机号已经充过值了,进行叠加
               phoneFareZSetOperations.incrementScore(key, fareDto, phoneFare.getFare().doubleValue());
            } else {
                // 如果用户没有充值过,就直接充值
                phoneFareZSetOperations.add(key, fareDto, phoneFare.getFare().doubleValue());
            }
        }
        return i > 0 ? phoneFare.getId() : 0;
    }


    /**
     * 2. 描述: 获取手机充值记录排行榜
     *    作者: xueyi
     *    日期: 2020/5/16 15:35
     *    参数: [isAsc]
     *    返回: java.util.Set<com.jbg.redis.model.entity.PhoneFare>
     */
    public  Set<PhoneFare>  getSortedSet(final Boolean isAsc){
        final String key = "SpringBootRedis:Sorted:PhoneFare:v1";
        ZSetOperations<String, PhoneFare> zSetOperations = redisTemplate.opsForZSet();
        // 获取键的长度
        Long size = zSetOperations.size(key);
        return isAsc ? this.getRange(key, size) :this.getReverseRange(key, size);
    }

    /**
     * 2. 描述: 获取手机充值记录排行榜v2
     *    作者: xueyi
     *    日期: 2020/5/16 15:35
     *    参数: [isAsc]
     *    返回: java.util.Set<com.jbg.redis.model.entity.PhoneFare>
     */
    public List<PhoneFare> getSortedSetV2(){
        List<PhoneFare> list = Lists.newLinkedList();
        final String key = "SpringBootRedis:Sorted:PhoneFare:v2";
        ZSetOperations<String, PhoneFareDto> zSetOperations = redisTemplate.opsForZSet();
        // 获取键的长度
        Long size = zSetOperations.size(key);
        // 从redis中取出带分数的排好序的成员
        Set<ZSetOperations.TypedTuple<PhoneFareDto>> setDto = zSetOperations.reverseRangeWithScores(key, 0, size);
        if (CollectionUtils.isNotEmpty(setDto)) {
            setDto.forEach(new Consumer<ZSetOperations.TypedTuple<PhoneFareDto>>() {
                @Override
                public void accept(ZSetOperations.TypedTuple<PhoneFareDto> tuple) {
                    PhoneFare phoneFare = new PhoneFare();
                    phoneFare.setFare(BigDecimal.valueOf(tuple.getScore()));
                    phoneFare.setPhone(tuple.getValue().getPhone());
                    list.add(phoneFare);
                }
            });
        }
        return list;
    }


    /*** 正序获取SortedSet里面的数据 ***/
    private Set<PhoneFare> getRange(String key, Long size){
        ZSetOperations<String, PhoneFare> zSetOperations = redisTemplate.opsForZSet();
       return  zSetOperations.range(key, 0L, size);
    }

    /*** 反序获取SortedSet里面的数据 ***/
    private Set<PhoneFare> getReverseRange(String key, Long size){
        ZSetOperations<String, PhoneFare> zSetOperations = redisTemplate.opsForZSet();
        return  zSetOperations.reverseRange(key, 0L, size);
    }
}
