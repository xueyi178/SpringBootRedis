package com.jbg.redis.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.jbg.redis.model.entity.Product;
import com.jbg.redis.model.mapper.ProductMapper;
import com.jbg.redis.server.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Redis中List数据类型的存储 Service
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/29 16:19
 */
@Service
@Slf4j
public class RedisListServiceImpl {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 添加商户商品信息
     *
     * @param product
     */
    @Transactional(rollbackFor = Exception.class)
    public int addProduct(Product product) {
        if (ObjectUtil.isNotEmpty(product)) {
            int falg = productMapper.insert(product);
            if (falg > 0) {
                Integer id = product.getId();
                product.setId(id);
                //存入Redis中的List列表中
                int i = this.pushRedisService(product);
                if (i == 0) {
                    return 0;
                }
            }
        }
        return 1;
    }


    /**
     * 把商户商品信息添加到redis中的List列表中
     *
     * @param product
     * @return
     */
    public int pushRedisService(final Product product) {
        ListOperations<String, Product> listOperations = redisTemplate.opsForList();
        Long flag = listOperations.leftPush(Constant.RedisListPrefix + product.getUserId(), product);
        if (flag.intValue() > 0) {
            return 0;
        }
        return 1;
    }


    /***
     * 获取历史发布的商品列表
     * @param product
     * @return
     */
    public List<Product> getHistoryProducts(final Integer userId) {
        //TODO: 创建一个list
        List<Product> list = Lists.newLinkedList();

        ListOperations<String, Product> listOperations = redisTemplate.opsForList();
        //Redis中的key
        String key = Constant.RedisListPrefix + userId;

        //TODO: userID = 10010 -> Rabbitmq -> Redis入门与实战 -> Springboot入门和实战 倒序取出
        //TODO: 获取List集合的大小
        //Long size = listOperations.size(key);
        //List<Product> listProduct = listOperations.range(key, 0, size);
        //log.info("-----倒序:{}", listProduct);

        //TODO: userID = 10010 -> Springboot入门和实战 -> Redis入门与实战 -> Rabbitmq 顺序取出
        //Collections.reverse(listProduct);

        //TODO:弹出来移除的方式
        Product entity = listOperations.rightPop(key);
        while (entity!=null){
            list.add(entity);
            entity = listOperations.rightPop(key);
        }

        if (!CollectionUtils.isEmpty(list)) {
            return list;
        }
        return Collections.EMPTY_LIST;
    }
}
