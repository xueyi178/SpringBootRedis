package com.jbg.redis.server.service;

import com.jbg.redis.model.entity.Item;

/**
 * <p>
 *      商品添加的业务逻辑层接口
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/15 15:29
 */
public interface IRedisStringService {

    /**
     * 添加商品信息
     * @param item
     * @return
     */
    public Integer addItem(Item item)throws Exception;

    /**
     * 读取商品信息
     * @param item
     * @return
     */
    public Item getItem(Integer id)throws Exception;
}
