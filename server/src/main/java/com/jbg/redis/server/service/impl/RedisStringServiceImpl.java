package com.jbg.redis.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbg.redis.model.entity.Item;
import com.jbg.redis.model.entity.Notice;
import com.jbg.redis.model.entity.Product;
import com.jbg.redis.model.mapper.ItemMapper;
import com.jbg.redis.model.mapper.NoticeMapper;
import com.jbg.redis.server.constant.Constant;
import com.jbg.redis.server.redis.StringRedisService;
import com.jbg.redis.server.service.IRedisStringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 商品添加的业务逻辑层实现类
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/15 15:41
 */
@Service
@Slf4j
public class RedisStringServiceImpl implements IRedisStringService {
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StringRedisService stringRedisService;

    /**
     * 方便的将模型对象转换为JSON，或者JSON生成相应的模型类
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 添加商品信息
     *
     * @param item
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addItem(Item item) throws Exception {
        item.setCreateTime(new Date());
        item.setId(null);
        int falg = itemMapper.insert(item);

        //  使用事物来保证写入redis和mysql的数据是一致的
        //  保证双写的一致性
        Integer id = item.getId();
        if (falg > 0) {
            //objectMapper.writeValueAsString(item) 序列化
            stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
        }
        return id;
    }

    /**
     * 获取商品信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Item getItem(Integer id) throws Exception {
        Item item = null;
        if (id != null) {
            if (stringRedisService.exist(id.toString())) {
                String result = (String) stringRedisService.get(id.toString());
                //反序列化
                if (StrUtil.isNotBlank(result)) {
                    item = objectMapper.readValue(result, Item.class);
                    log.info("从缓存中获取商品数据{}", item);
                }
            } else {
                item = itemMapper.selectByPrimaryKey(id);
                log.info("从数据库中查询出数据{}", item);
                if (item != null) {
                    stringRedisService.put(id.toString(), objectMapper.writeValueAsString(item));
                }
            }
        }
        return item;
    }


}