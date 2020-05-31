package com.jbg.redis.server.controller;

import cn.hutool.json.JSONObject;
import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.model.entity.RedRecord;
import com.jbg.redis.server.service.impl.RedPacketServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *      抢红包系统
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 15:24
 */
@RestController
@RequestMapping(value = "/red/packet")
@Slf4j
public class RedPacketController {

    @Autowired
    private RedPacketServiceImpl redPacketService;

   /**
    * 1. 描述: 发红包业务模块
    *    作者: xueyi
    *    日期: 2020/5/31 15:48
    *    参数: [dto, result]
    *    返回: com.jbg.redis.api.response.BaseResponse
    */
    @PostMapping(value = "/hand/out")
    public BaseResponse handOut(@RequestBody RedRecord dto, BindingResult result){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        JSONObject jsonObject =  new JSONObject();
        try {
            jsonObject.put("redKey",redPacketService.handOut(dto));
        }catch (Exception e){
            log.error("发红包业务模块发生异常：dto={} ",dto,e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        response.setData(jsonObject);
        return response;
    }


    /**
     * 2. 描述: 抢红包业务模块
     *    作者: xueyi
     *    日期: 2020/5/31 15:48
     *    参数: [dto, result]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @GetMapping(value = "/rob")
    public BaseResponse rob(@RequestParam Integer userId, @RequestParam String redKey){

        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            //Integer redValue=redPacketService.rob(userId,redKey);
            //Integer redValue=redPacketService.robV2(userId,redKey);
            Integer redValue=redPacketService.robV3(userId,redKey);
            if (redValue!=null){
                response.setData(redValue);
            }else{
                return new BaseResponse(StatusCode.Fail.getCode(),"红包已被抢完！");
            }

        }catch (Exception e){
            log.error("抢红包业务模块发生异常：userId={} redKey={}",userId,redKey,e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

}
