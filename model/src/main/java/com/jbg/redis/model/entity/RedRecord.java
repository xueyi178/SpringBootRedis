package com.jbg.redis.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * <p>
 *  发红包记录表
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 15:21
 */
@Data
public class RedRecord {
    private Integer id;

    private Integer userId;

    private String redPacket;

    private Integer total;

    private BigDecimal amount;

    private Byte isActive;

    private Date createTime;
}