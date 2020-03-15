package com.jbg.redis.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 红包明细金额
 * @author Lenovo
 */
@Data
@ToString
public class Item implements Serializable {

    private Integer id;

    @NotNull(message = "商品编码不能为空")
    private String code;

    @NotNull(message = "商品名称不能为空")
    private String name;

    private Date createTime;
}