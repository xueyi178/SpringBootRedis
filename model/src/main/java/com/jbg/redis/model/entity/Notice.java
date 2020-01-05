package com.jbg.redis.model.entity;

import lombok.Data;

/**
 * 通告
 */
@Data
public class Notice {
    private Integer id;

    private String title;

    private String content;

    private Byte isActive;
}