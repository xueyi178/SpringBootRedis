package com.jbg.redis.server.controller;

import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.model.entity.SysConfig;
import com.jbg.redis.server.service.impl.RedisHashServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Redis中Hash数据类型的存储 Controller 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/5/17 14:47
 */
@RestController
@RequestMapping(value = "/hash")
public class RedisHashController {

    @Resource
    private RedisHashServiceImpl redisHashService;

    /**
     * 1. 描述: 添加数据字典到hash缓存中
     * 作者: xueyi
     * 日期: 2020/5/17 15:15
     * 参数: [user]
     * 返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/put", method = RequestMethod.POST)
    public BaseResponse put(@RequestBody SysConfig sysConfig) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            int i = redisHashService.addSysConfig(sysConfig);
            if (i <= 0) {
                response.setData("数据字典添加失败!");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }


    /**
     * 2. 描述: 获取所有redis中Hash的缓存数据
     * 作者: xueyi
     * 日期: 2020/4/18 16:30
     * 参数: []
     * 返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public BaseResponse get() {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            Map<String, List<SysConfig>> all = redisHashService.getAll();

            if (CollectionUtils.isNotEmpty(Collections.singleton(all))) {
                response.setData(all);
            } else {
                response.setData("获取缓存中的数据失败!");
            }

        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    /**
     * 3. 描述: 获取所有redis中Hash特定的Type缓存数据
     * 作者: xueyi
     * 日期: 2020/4/18 16:30
     * 参数: []
     * 返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/get/type", method = RequestMethod.GET)
    public BaseResponse getTyep(@RequestParam String type) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            List<SysConfig> byTypeList = redisHashService.getByType(type);

            if (CollectionUtils.isNotEmpty(byTypeList)) {
                response.setData(byTypeList);
            } else {
                response.setData("获取所有redis中Hash特定的Type缓存数据失败!");
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}
