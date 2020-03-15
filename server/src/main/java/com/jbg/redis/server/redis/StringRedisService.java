package com.jbg.redis.server.redis;

import com.jbg.redis.server.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  Redis中String的模板
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/15 15:44
 */
@Component
public class StringRedisService {
    private static final Logger log= LoggerFactory.getLogger(StringRedisService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations getOperation(){
        return stringRedisTemplate.opsForValue();
    }

    public void put(final String key,final String value) throws Exception{
        getOperation().set(Constant.RedisStringPrefix+key,value);
    }

    public Object get(final String key) throws Exception{
        return getOperation().get(Constant.RedisStringPrefix+key);
    }

    public Boolean exist(final String key) throws Exception{
        return stringRedisTemplate.hasKey(Constant.RedisStringPrefix+key);
    }

    public void expire(final String key,final Long expireSeconds) throws Exception{
        stringRedisTemplate.expire(key,expireSeconds, TimeUnit.SECONDS);
    }
}
