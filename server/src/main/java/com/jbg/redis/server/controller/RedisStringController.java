package com.jbg.redis.server.controller;

import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.model.entity.Item;
import com.jbg.redis.server.service.IRedisStringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  Redis中String数据类型的存储
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/15 15:16
 */
@RestController
@RequestMapping(value = "/string")
@Slf4j
public class RedisStringController {

    @Autowired
    private IRedisStringService redisStringService;

    /**
     * 1. 添加商品详情
     * @return
     */
    @PostMapping(value = "/put")
    public BaseResponse put(@RequestBody Item item, BindingResult result){

        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);

        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.InvalidParams);
        }

        try {
            log.info("-----商品信息----{}", item);
            Integer falg = redisStringService.addItem(item);
            if (falg> 0) {
                return baseResponse;
            }
        } catch (Exception e) {
            log.error("---字符串String实战--商品添加详情存储--添加--发生异常");
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }


    /**
     * 2. 获取商品详情
     * @return
     */
    @PostMapping(value = "/get")
    public BaseResponse get(@RequestParam Integer id){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);

        try {
            Item item = redisStringService.getItem(id);
            if (item != null) {
                baseResponse.setData(item);
            }
        } catch (Exception e) {
            log.error("---字符串String实战--获取商品详情--获取--发生异常");
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }
}
