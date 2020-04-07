package com.jbg.redis.server.controller;

import com.jbg.redis.api.response.BaseResponse;
import com.jbg.redis.api.response.StatusCode;
import com.jbg.redis.model.entity.Product;
import com.jbg.redis.server.service.impl.RedisListServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  Redis中List数据类型的存储 Controller 接口
 * </p>
 *
 * @author xueyi
 * @since 2020/4/7 17:48
 */
@Slf4j
@RestController
@RequestMapping(value = "/list")
public class RedisListController {

    @Autowired
    private RedisListServiceImpl redisListService;

    /**
     * 1. 描述: 添加List的数据
     *    作者: xueyi
     *    日期: 2020/4/7 17:46
     *    参数: [product, result]
     *    返回: com.jbg.redis.api.response.BaseResponse
     */
    @PostMapping(value = "/put")
    public BaseResponse put(@RequestBody Product  product, BindingResult result){

        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);

        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.InvalidParams);
        }

        try {
            log.info("-----商户商品----{}", product);
            int i = redisListService.addProduct(product);
            if (i != 0) {
                baseResponse = new BaseResponse(StatusCode.Fail.getCode(), "添加商户商品失败");
            }
        } catch (Exception e) {
            log.error("---List实战--商户商品添加详情存储--添加--发生异常");
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }


    /**
     * 2. 获取商户商品列表
     * @return
     */
    @PostMapping(value = "/get")
    public BaseResponse get(@RequestParam Integer id){
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);

        try {
            List<Product> historyProducts = redisListService.getHistoryProducts(id);
            if (CollectionUtils.isNotEmpty(historyProducts)) {
               baseResponse.setData(historyProducts);
            }
        } catch (Exception e) {
            log.error("---List实战战--获取商户商品列表----发生异常");
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return baseResponse;
    }
}
