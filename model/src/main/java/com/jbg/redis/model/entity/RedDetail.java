package com.jbg.redis.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * <p>
 *  红包明细表
 * </p>
 *
 * @author xueyi
 * @since 2020/5/31 15:21
 */
@Data
public class RedDetail {
    private Integer id;

    private Integer recordId;

    private BigDecimal amount;

    private Byte isActive;

    private Date createTime;
}