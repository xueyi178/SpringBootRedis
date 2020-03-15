package com.jbg.redis.test;

import com.jbg.redis.model.ModelApplication;
import com.jbg.redis.server.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.internal.Classes;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.swing.*;

/**
 * <p>
 * String 字符串的测试
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/15 14:45
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServerApplication.class)
@Slf4j
public class RedisTestOne {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void method1() {
        log.info("-----开始字符串的测试-----");
        final String key = "SpringRedis:Order:001";
        ValueOperations opsForValue = redisTemplate.opsForValue();

        opsForValue.set(key, 1000L);
        log.info("-----当前key{},对应的value{}-----", key, opsForValue.get(key));

        opsForValue.increment(key, 13332L);
        log.info("-----当前key{},对应的value{}-----", key, opsForValue.get(key));
    }
}
