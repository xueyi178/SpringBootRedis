package com.jbg.redis.server.scheduler;

import com.jbg.redis.model.Dto.PhoneFareDto;
import com.jbg.redis.model.entity.PhoneFare;
import com.jbg.redis.model.mapper.PhoneFareMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
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


    //@Scheduled(cron = "0/5 * * * * ?")
    public void sortFareScheduler(){
        log.info("--补偿性手机号码充值排行榜-定时任务");

        this.cacheSortResult();
    }

    @Async("threadPoolTaskExecutor")
    public void cacheSortResult(){
        try {
            ZSetOperations<String, PhoneFareDto> zSetOperations=redisTemplate.opsForZSet();

            List<PhoneFare> list=phoneFareMapper.listPhoneFare();
            if (CollectionUtils.isNotEmpty(list)){
                final String key = "SpringBootRedis:Sorted:PhoneFare:v2";
                redisTemplate.delete(key);
                list.forEach(fare -> {
                    PhoneFareDto dto=new PhoneFareDto(fare.getPhone());
                    zSetOperations.add(key,dto,fare.getFare().doubleValue());
                });
            }
        }catch (Exception e){
            log.error("--补偿性手机号码充值排行榜-定时任务-发生异常：",e.fillInStackTrace());
        }
    }
}

































