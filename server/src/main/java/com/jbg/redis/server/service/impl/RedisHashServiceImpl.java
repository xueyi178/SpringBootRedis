package com.jbg.redis.server.service.impl;

import com.jbg.redis.model.entity.SysConfig;
import com.jbg.redis.model.mapper.SysConfigMapper;
import com.jbg.redis.server.redis.HashService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Redis中Hash数据类型的存储 ServiceImpl 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/5/17 14:48
 */
@Service
@Slf4j
public class RedisHashServiceImpl {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Resource
    private HashService hashService;

    /**
     * 1. 描述: 添加数据字典到hash缓存中
     * 作者: xueyi
     * 日期: 2020/5/17 15:17
     * 参数: [sysConfig]
     * 返回: int
     */
    @Transactional(rollbackFor = Exception.class)
    public int addSysConfig(SysConfig sysConfig) {
        sysConfig.setCreateTime(LocalDate.now().toDate());
        int i = sysConfigMapper.insertSelective(sysConfig);
        if (i > 0) {
            hashService.cacheConfigMap();
        }
        return i > 0 ? sysConfig.getId() : 0;
    }

    /**
     * 2. 描述: 获取所有redis中的缓存数据
     *    作者: xueyi
     *    日期: 2020/5/17 15:55
     *    参数: []
     *    返回: java.util.Map<java.lang.String,java.util.List<com.jbg.redis.model.entity.SysConfig>>
     */
    public Map<String, List<SysConfig>> getAll(){
        Map<String, List<SysConfig>> allCacheConfig = hashService.getAllCacheConfig();
        if (CollectionUtils.isNotEmpty(Collections.singleton(allCacheConfig))) {
            return allCacheConfig;
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * 3. 描述: 根据特定的key从缓存中获取数据字典
     *    作者: xueyi
     *    日期: 2020/5/17 16:06
     *    参数: [type]
     *    返回: java.util.List<com.jbg.redis.model.entity.SysConfig>
     */
    public List<SysConfig> getByType(String type) {
       return hashService.getCacheConfigByType(type);
    }
}
