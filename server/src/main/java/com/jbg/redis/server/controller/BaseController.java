package com.jbg.redis.server.controller;

import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName BaseController
 * @ClassName BaseController
 * @Description TODO
 * @Author Lenovo
 * @Date 2020/1/5 15:56
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/base")
@Slf4j
public class BaseController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String RedisHelloWorldKey = "StringRedis:HelloWorld";

    @GetMapping(value = "/info")
    public BaseResponse info(){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);
        try {
            return baseResponse;
        } catch (Exception e) {
            log.info("Reids快速入门的状态编码{},异常信息{}", StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }

    /**
     * 存入redis
     * @param helloName
     * @return
     */
    @GetMapping(value = "/hello/world/put")
    public BaseResponse helloWordPut(@RequestParam String helloName){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);
        try {
            stringRedisTemplate.opsForValue().set(RedisHelloWorldKey, helloName);
            baseResponse.setData(helloName);
        } catch (Exception e) {
            log.info("存入redis异常信息{}", e.getMessage());
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }

    /**
     * 获取redis
     * @param
     * @return
     */
    @GetMapping(value = "/hello/world/get")
    public BaseResponse helloWordGet(){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);
        try {
            baseResponse.setData(stringRedisTemplate.opsForValue().get(RedisHelloWorldKey));
        } catch (Exception e) {
            log.info("获取redis异常信息{}", e.getMessage());
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }
}
