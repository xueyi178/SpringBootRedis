package com.jbg.redis.server.redis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jbg.redis.model.entity.SysConfig;
import com.jbg.redis.model.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *      Redis中Hash的异步调用
 * </p>
 *
 * @author xueyi
 * @since 2020/5/17 15:46
 */
@Service
@Slf4j
public class HashService {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SysConfigMapper sysConfigMapper;

    /**
     * redis中的key
     **/
    final String key = "SpringBootRedis:Hash:SysConfig";

    /**
     * 1. 描述: 时实获取所有有效的数据字典列表 - 转化为map-存入hash缓存中
     *    作者: xueyi
     *    日期: 2020/5/17 15:49
     *    参数: []
     *    返回: void
     */
    @Async
    public void cacheConfigMap() {
        try {
            List<SysConfig> sysConfigs = sysConfigMapper.selectActiveConfigs();
            // 判断集合是否为空
            if (CollectionUtils.isNotEmpty(sysConfigs)) {
                //创建集合
                Map<String, List<SysConfig>> map = Maps.newHashMap();
                //遍历集合
                sysConfigs.forEach(config -> {
                    List<SysConfig> configs = map.get(config.getType());
                    if (CollectionUtils.isEmpty(configs)) {
                        configs = Lists.newLinkedList();
                    }
                    configs.add(config);
                    //添加到map中
                    map.put(config.getType(), configs);
                });
                // 存储到hash缓存中
                HashOperations<String, String, List<SysConfig>> hashOperations = redisTemplate.opsForHash();
                hashOperations.delete(key, map);
                hashOperations.putAll(key, map);
            }
        } catch (Exception e) {
            log.info("时实获取所有有效的数据字典列表 - 转化为map-存入hash缓存中{}", e.getMessage());
        }
    }

    /**
     * 2. 描述: 从缓存中获取所有的数据字典
     *    作者: xueyi
     *    日期: 2020/5/17 15:50
     *    参数: []
     *    返回: java.util.Map<java.lang.String,java.util.List<com.jbg.redis.model.entity.SysConfig>>
     */
    public Map<String, List<SysConfig>> getAllCacheConfig(){
        try {
            HashOperations<String,String, List<SysConfig>>  hashOperations = redisTemplate.opsForHash();
            return hashOperations.entries(key);
        } catch (Exception e) {
            log.info("从缓存中获取所有的数据字典时发生异常信息:[]", e.getMessage());
        }
        return null;
    }

    /**
     * 3. 描述: 根据特定的key从缓存中获取数据字典
     *    作者: xueyi
     *    日期: 2020/5/17 16:06
     *    参数: [type]
     *    返回: java.util.List<com.jbg.redis.model.entity.SysConfig>
     */
    public List<SysConfig> getCacheConfigByType(final String type) {
        LinkedList<SysConfig> lists = Lists.newLinkedList();
        HashOperations<String,String, List<SysConfig>>  hashOperations = redisTemplate.opsForHash();
        List<SysConfig> sysConfigs = hashOperations.get(key, type);
        if (CollectionUtils.isNotEmpty(sysConfigs)) {
            return lists;
        }
        return Collections.EMPTY_LIST;
    }
}
