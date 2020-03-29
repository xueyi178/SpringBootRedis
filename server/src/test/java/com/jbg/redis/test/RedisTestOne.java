package com.jbg.redis.test;

import com.jbg.redis.server.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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

    @Test
    public void method2() {
        log.info("-----开始List的列表测试-----");
        final String key = "SpringRedis:List:10010";

        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        List<String> list = Lists.newArrayList("c","d", "e");
        listOperations.leftPush(key, "a");
        listOperations.leftPush(key, "b");
        listOperations.leftPushAll(key, list);

        log.info("-----当前列表元素的个数:{}", listOperations.size(key));
        log.info("-----当前列表中的元素:{}", listOperations.range(key,0,10));

        log.info("-----当前列表中下标为0的元素:{}", listOperations.index(key,0));
        log.info("-----当前列表中下标为4的元素:{}", listOperations.index(key,4));
        log.info("-----当前列表中下标为10的元素:{}", listOperations.index(key,10));

        log.info("-----当前列表中从右边弹出来:{}", listOperations.rightPop(key));

        listOperations.set(key, 0L, "100");
        log.info("-----当前列表中下标为0的元素:{}", listOperations.index(key,0));

        listOperations.remove(key, 0, 100);
        log.info("-----当前列表中的元素:{}", listOperations.range(key,0,10));

        listOperations.remove(key, 0, 100);
        log.info("-----当前列表中的元素:{}", listOperations.range(key,0,10));
    }
}
