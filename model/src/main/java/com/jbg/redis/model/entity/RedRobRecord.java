package com.jbg.redis.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * <p>
 *  抢红包记录
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 15:22
 */
@Data
public class RedRobRecord {
    private Integer id;

    private Integer userId;

    private String redPacket;

    private BigDecimal amount;

    private Date robTime;

    private Byte isActive;
}