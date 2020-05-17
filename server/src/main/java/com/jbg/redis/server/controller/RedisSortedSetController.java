package com.jbg.redis.server.controller;

import cn.hutool.json.JSONObject;
import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.model.entity.PhoneFare;
import com.jbg.redis.server.service.impl.RedisSortedSetImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *    Redis中SortedSet数据类型的存储 Controller 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/5/16 14:55
 */
@RestController
@RequestMapping(value = "/sortedSet")
public class RedisSortedSetController {

    @Autowired
    private RedisSortedSetImpl redisSortedSet;


    /**
     * 1. 描述: 新增手机充值记录
     *    作者: xueyi
     *    日期: 2020/5/16 15:09
     *    参数: [phoneFare]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/put",method = RequestMethod.POST)
    public BaseResponse put(@RequestBody PhoneFare phoneFare){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Integer integer = redisSortedSet.addRecord(phoneFare);
            if (integer.intValue() <= 0) {
             response.setMsg("新增手机充值记录失败!");
            }
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 1. 描述: 新增手机充值记录
     *    作者: xueyi
     *    日期: 2020/5/16 15:09
     *    参数: [phoneFare]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/put2",method = RequestMethod.POST)
    public BaseResponse put2(@RequestBody PhoneFare phoneFare){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Integer integer = redisSortedSet.addRecordV2(phoneFare);
            if (integer.intValue() <= 0) {
                response.setMsg("新增手机充值记录失败!");
            }
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 2. 描述: 获取手机充值记录排行榜
     *    作者: xueyi
     *    日期: 2020/5/16 15:38
     *    参数: []
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public BaseResponse get(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("SortedSetFaresAsc", redisSortedSet.getSortedSet(true));
            responseJson.put("SortedSetFaresDesc", redisSortedSet.getSortedSet(false));
            response.setData(responseJson);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    /**
     * 2. 描述: 获取手机充值记录排行榜v2
     *    作者: xueyi
     *    日期: 2020/5/16 15:38
     *    参数: []
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @RequestMapping(value = "/getV2",method = RequestMethod.GET)
    public BaseResponse getV2(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("SortedSetFaresV2", redisSortedSet.getSortedSetV2());
            response.setData(responseJson);
        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}
