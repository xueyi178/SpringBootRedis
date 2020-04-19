package com.jbg.redis.server.controller;

import cn.hutool.json.JSONObject;
import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.model.entity.Problem;
import com.jbg.redis.model.entity.User;
import com.jbg.redis.server.service.impl.ProblemServiceImpl;
import com.jbg.redis.server.service.impl.RedisSetServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * <p>
 *     Redis中Set数据类型的存储  Controller 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/4/18 16:26
 */
@RestController
@RequestMapping(value = "/set")
@Slf4j
public class RedisSetController {

    @Autowired
    private RedisSetServiceImpl redisSetService;

    @Autowired
    private ProblemServiceImpl problemService;


    /**
     * 1. 描述: 提交用户注册
     *    作者: xueyi
     *    日期: 2020/4/18 16:30
     *    参数: [user, result]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/put",method = RequestMethod.POST)
    public BaseResponse put(@RequestBody User user){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("----用户注册信息：{}",user);
            Integer integer = redisSetService.registerUser(user);
            if (integer.intValue() == 0) {
                response=new BaseResponse(StatusCode.Fail.getCode(),"邮箱已经存在,请勿重复添加");
            }
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 2. 描述: 取出缓存集合Set中所有已注册的用户的邮箱列表
     *    作者: xueyi
     *    日期: 2020/4/18 16:30
     *    参数: []
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    //TODO:取出缓存集合Set中所有已注册的用户的邮箱列表
    @RequestMapping(value = "get",method = RequestMethod.GET)
    public BaseResponse get(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Set<String> emails = redisSetService.getEmails();
            response.setData(emails);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     *    描述: 从缓存中获取问题
     *    作者: xueyi
     *    日期: 2020/4/19 15:36
     *    参数: []
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "getRandomProblem",method = RequestMethod.GET)
    public BaseResponse getRandomProblem(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Problem randomEntity = problemService.getRandomEntity();
            response.setData(randomEntity);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    /**
     *    描述: 从缓存中获取随机 乱序的题目
     *    作者: xueyi
     *    日期: 2020/4/19 15:36
     *    参数: []
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @PostMapping(value = "listRandom")
    public BaseResponse listRandom(@RequestBody JSONObject requestJson){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Set<Problem> problems = problemService.listRandomEntity(requestJson.getInt("total"));
            response.setData(problems);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}
