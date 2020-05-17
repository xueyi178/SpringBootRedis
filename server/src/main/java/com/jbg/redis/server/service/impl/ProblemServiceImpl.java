package com.jbg.redis.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Sets;
import com.jbg.redis.model.entity.Problem;
import com.jbg.redis.model.mapper.ProblemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;

/**
 * <p>
 *   问题服务的 ServiceImpl 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/4/19 15:16
 */
@Service
@Slf4j
public class ProblemServiceImpl {

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //项目一启动的时候从数据库拉出问题,并塞入set集合中
    @PostConstruct
    public void init(){
        this.initDBToCache();
    }

    /**
     *    描述: 项目一启动的时候从数据库拉出问题,并塞入set集合缓存中
     *    作者: xueyi
     *    日期: 2020/4/19 15:34
     *    参数: []
     *    返回: void
     */
    private void initDBToCache(){
        try {
            //启动的时候清楚key
            redisTemplate.delete(Arrays.asList("SpringBootRedis:Set:Problem", "SpringBootRedis:Set:Problem"));
            Set<Problem> problemSet = problemMapper.getAll();
            SetOperations setOperations = redisTemplate.opsForSet();
            if (ObjectUtil.isNotEmpty(problemSet)) {
                // 随机弹出问题
                problemSet.forEach(problem ->  setOperations.add("SpringBootRedis:Set:Problem", problem));
                // 随机列表,无序
                problemSet.forEach(problem ->  setOperations.add("SpringBootRedis:Set:Problems", problem));
            }
        } catch (Exception e) {
            log.info("数据库拉出问题,放入缓存出现异常:[{}]", e.getMessage());
        }
    }

    /**
     *    描述: 从缓存中获取随机问题
     *    作者: xueyi
     *    日期: 2020/4/19 15:34
     *    参数: []
     *    返回: com.jbg.redis.model.entity.Problem
     */
    public Problem getRandomEntity(){
        Problem problem = null;
        try {
            SetOperations setOperations = redisTemplate.opsForSet();
            Long size = setOperations.size("SpringBootRedis:Set:Problem");
            if (size > 0 ) {
                //弹出问题
                problem = (Problem) setOperations.pop("SpringBootRedis:Set:Problem");
            }else{
                this.initDBToCache();
                problem = (Problem) setOperations.pop("SpringBootRedis:Set:Problem");
            }
        } catch (Exception e) {
            log.info("从缓存中获取问题,出现异常:[{}]", e.getMessage());
        }

        return problem;
    }


    /**
     *    描述: 从缓存中获取随机 乱序的问题列表
     *    作者: xueyi
     *    日期: 2020/4/19 15:34
     *    参数: []
     *    返回: com.jbg.redis.model.entity.Problem
     */
    public Set<Problem> listRandomEntity(Integer total){
        Set<Problem> problemHashSet = Sets.newHashSet();
        try {
            SetOperations setOperations = redisTemplate.opsForSet();
            problemHashSet = (Set<Problem>) setOperations.distinctRandomMembers("SpringBootRedis:Set:Problems", total);
        } catch (Exception e) {
            log.info("从缓存中获取问题,出现异常:[{}]", e.getMessage());
        }
        return problemHashSet;
    }
}
