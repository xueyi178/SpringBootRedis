package com.jbg.redis.server.controller;

import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.server.service.impl.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 17:22
 */
@RestController
@RequestMapping("item")
@Slf4j
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 1. 描述: 获取详情-@Cacheable
     *    作者: xueyi
     *    日期: 2020/5/31 17:25
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @GetMapping(value = "info")
    public BaseResponse info(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(itemService.getInfo(id));

        }catch (Exception e){
            log.error("--商品详情controller-详情-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 2. 描述: 获取详情
     *    作者: xueyi
     *    日期: 2020/5/31 17:25
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @GetMapping(value = "info/v2")
    public BaseResponse infoV2(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            response.setData(itemService.getInfoV2(id));

        }catch (Exception e){
            log.error("--商品详情controller-详情-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 3. 描述: 删除
     *    作者: xueyi
     *    日期: 2020/5/31 17:25
     *    参数: [id]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @GetMapping(value = "delete")
    public BaseResponse delete(@RequestParam Integer id){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            itemService.delete(id);

        }catch (Exception e){
            log.error("--商品详情controller-删除-发生异常：",e.fillInStackTrace());
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }
}
