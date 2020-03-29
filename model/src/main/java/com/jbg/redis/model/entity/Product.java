package com.jbg.redis.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * <p>
 *  商户商品的实体类
 * </p>
 *
 * @author Xueyi
 * @since 2020/3/15 15:16
 */
@Data
@ToString
public class Product implements Serializable {
    private Integer id;

    @NotNull(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "所属商户id不能为空")
    private Integer userId;

    private Integer scanTotal;

    private Byte isActive;

}