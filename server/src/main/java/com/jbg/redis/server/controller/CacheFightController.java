package com.jbg.redis.server.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.server.service.impl.CacheFightServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  Rrdis缓存穿透解决办法
 * </p>
 *
 * @author xueyi
 * @since 2020/5/30 15:15
 */
@RestController
@RequestMapping(value="/cache/fight")
@Slf4j
public class CacheFightController {

    @Resource
    private CacheFightServiceImpl cacheFightService;

    //限流组件,每秒放进一个令牌
    /**
     *  缓存穿透的第一种解决办法,使用Guava-RateLimiter来做限流操作,
     *      Guava-RateLimiter的底层实现就是令牌桶算法,单线程的
     *      在分布式的集群的时候,性能不是很好,考虑用SpringCloud的限流组件来控制
     *
     */
    private static final RateLimiter LIMITER = RateLimiter.create(1);

    /**
     * 1. 描述: 缓存穿透
     *    作者: xueyi
     *    日期: 2020/5/30 15:19
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @GetMapping(value = "/through")
    public BaseResponse get(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
           //TODO:实际应用场景中 - 限流（组件-hystrix、guava提供的RateLimiter）
            //TODO:实际应用场景中 - 限流：guava提供的RateLimiter，尝试获取令牌：此处是单线程服务的限流,内部采用令牌捅算法实现
            // 秒级并发,只通过一次
            if (LIMITER.tryAcquire(1)){
                //if (LIMITER.tryAcquire(10L, TimeUnit.SECONDS)){
                response.setData(cacheFightService.getItemV2(id));
            }
        }catch (Exception e){
            log.error("--典型应用场景实战-缓存穿透-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    /**
     * 2. 描述: 缓存击穿
     *    作者: xueyi
     *    日期: 2020/5/30 15:19
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @GetMapping(value = "/through/beat")
    public BaseResponse get2(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(cacheFightService.getItemCacheBeat(id));
        }catch (Exception e){
            log.error("--典型应用场景实战-缓存穿透-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}
