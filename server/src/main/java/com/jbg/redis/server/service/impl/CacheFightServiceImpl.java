package com.jbg.redis.server.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbg.redis.model.entity.Item;
import com.jbg.redis.model.mapper.ItemMapper;
import com.jbg.redis.server.redis.StringRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  Rrdis缓存穿透解决办法
 * </p>
 *
 * @author xueyi
 * @since 2020/5/30 15:15
 */
@Service
@Slf4j
public class CacheFightServiceImpl {


    @Autowired
    private StringRedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemMapper itemMapper;



    /**
     * 1. 描述: 缓存穿透
     *    作者: xueyi
     *    日期: 2020/5/30 15:19
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    public Item getItem(Integer id) throws Exception{
        Item item=null;
        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();
                //log.info("从缓存中去查询,id:{},查询的结果为:{}",id, result);
                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---缓存穿透，从数据库查询：id={}",id);
                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }
            }
        }
        return item;
    }

    /**
     * 2. 描述: 缓存穿透的第一种解决办法,在数据库中查询不到数据的时候,也存入到缓存中
     *    作者: xueyi
     *    日期: 2020/5/30 15:19
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    public Item getItemV1(Integer id) throws Exception{
        Item item=null;
        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();
                //log.info("从缓存中去查询,id:{},查询的结果为:{}",id, result);
                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---缓存穿透，从数据库查询：id={}",id);
                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }else{
                    // 在数据库中没有查到记录,也存在redis中,加一个数据的过期时间
                    redisService.put(id.toString(),"");
                    // 把id当作key,存在缓存中
                    redisService.expire(id.toString(), 3600L);
                }
            }
        }
        return item;
    }

    /**
     * 3. 描述: 缓存穿透的第一种解决办法,使用Guava-RateLimiter来做限流操作,
     *          Guava-RateLimiter的底层实现就是令牌桶算法,单线程的
     *          在分布式的集群的时候,性能不是很好,考虑用SpringCloud的限流组件来控制
     *    作者: xueyi
     *    日期: 2020/5/30 15:19
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    public Item getItemV2(Integer id) throws Exception{
        Item item=null;
        if (id!=null){
            if (redisService.exist(id.toString())){
                String result=redisService.get(id.toString()).toString();
                //log.info("从缓存中去查询,id:{},查询的结果为:{}",id, result);
                if (StrUtil.isNotBlank(result)){
                    item=objectMapper.readValue(result,Item.class);
                }
            }else{
                log.info("---缓存穿透，从数据库查询：id={}",id);
                item=itemMapper.selectByPrimaryKey(id);
                if (item!=null){
                    redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                }else{
                    // 在数据库中没有查到记录,也存在redis中,加一个数据的过期时间
                    redisService.put(id.toString(),"");
                    // 把id当作key,存在缓存中
                    redisService.expire(id.toString(), 3600L);
                }
            }
        }
        return item;
    }


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /** 雪花算法  **/
    private static final Snowflake SNOWFLAKE=new Snowflake(3,2);

    /**
     *    描述: 缓存击穿-查询商品详情
     *    作者: xueyi
     *    日期: 2020/5/31 10:42
     *    参数: [id]
     *    返回: com.jbg.redis.model.entity.Item
     */
    public Item getItemCacheBeat(Integer id) throws Exception{
        Item item=null;

        if (redisService.exist(id.toString())){
            String result=redisService.get(id.toString()).toString();

            if (StrUtil.isNotBlank(result)){
                item=objectMapper.readValue(result,Item.class);
            }
        }else{
            //TODO:分布式锁的实现 - 同一时刻只能保证 拥有该key 的一个线程进入 执行共享的业务代码
            //TODO:SETNX、EXPIRE、DELETE ； Redis单线程（它的操作是原子性操作） - io多路复用（单核cpu 却支持多任务、多用户的使用，频繁切换很快，用户识别不了）
            //使用雪花算法来生成值
            String value=SNOWFLAKE.nextIdStr();
            ValueOperations<String,String> valueOperations=stringRedisTemplate.opsForValue();
            Boolean lock=valueOperations.setIfAbsent("Springboot:CacheThrough:Lockkey",value);
            try {
                if (lock){
                    stringRedisTemplate.expire("Springboot:CacheThrough:Lockkey",10L, TimeUnit.SECONDS);
                    log.info("---缓存击穿，从数据库查询：id={}",id);
                    item=itemMapper.selectByPrimaryKey(id);
                    if (item!=null){
                        redisService.put(id.toString(),objectMapper.writeValueAsString(item));
                    }else{
                        redisService.put(id.toString(),"");
                        redisService.expire(id.toString(),3600L);
                    }
                }
            }finally {
                String currValue=valueOperations.get("Springboot:CacheThrough:Lockkey");
                //先要获取到key,判断value是否相等,才去删除key
                if (StrUtil.isNotBlank(currValue) && currValue.equals(value)){
                    stringRedisTemplate.delete("Springboot:CacheThrough:Lockkey");
                }
            }
        }
        return item;
    }
}
