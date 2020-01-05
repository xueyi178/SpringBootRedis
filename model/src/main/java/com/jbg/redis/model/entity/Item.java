package com.jbg.redis.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * 红包明细金额
 */
@Data
public class Item {
    private Integer id;

    private String code;

    private String name;

    private Date createTime;
}