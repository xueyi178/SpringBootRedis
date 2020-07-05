package com.jbg.redis.server.service.impl;

import com.jbg.redis.model.entity.Item;
import com.jbg.redis.model.mapper.ItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 17:23
 */
@Service
@Slf4j
public class ItemService {

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 1. 描述: 获取商品详情v1
     *    作者: xueyi
     *    日期: 2020/5/31 17:26
     *    参数: [id]
     *    返回: com.jbg.redis.model.entity.Item
     */
    //TODO:value-必填；key-支持 springEL表达式
    //TODO:java.lang.ClassCastException - 因为devtools为了实现重新装载class自己实现了一个类加载器，所以会导致类型强转异常
    //TODO: @Cacheable 缓存存在时，则直接取缓存，不存在，走方法体的逻辑
    @Cacheable(value = "SpringBootRedis:Item",key = "#id",unless = "#result == null")
    public Item getInfo(Integer id){
        Item entity=itemMapper.selectByPrimaryKey(id);
        log.info("--@Cacheable走数据库查询：{}",entity);
        return entity;
    }

    /**
     * 2. 描述: 获取商品详情v2
     *    作者: xueyi
     *    日期: 2020/5/31 17:26
     *    参数: [id]
     *    返回: com.jbg.redis.model.entity.Item
     */
    //TODO: @CachePut: 不管你缓存存不存在，都会put到缓存中去
    @CachePut(value = "SpringBootRedis:Item",key = "#id",unless = "#result == null")
    public Item getInfoV2(Integer id){
        Item entity=itemMapper.selectByPrimaryKey(id);

        entity.setCode(UUID.randomUUID().toString());

        log.info("学院 @CachePut走数据库查询:[{}]--",entity);
        return entity;
    }

    /**
     * 3. 描述: 删除缓存
     *    作者: xueyi
     *    日期: 2020/5/31 17:27
     *    参数: [id]
     *    返回: void
     */
    //TODO：失效/删除缓存
    @CacheEvict(value = "SpringBootRedis:Item",key = "#id")
    public void delete(Integer id){
        itemMapper.deleteByPrimaryKey(id);
        log.info("--@CacheEvict删除功能：id={}",id);
    }
}
