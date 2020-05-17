package com.jbg.redis.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *  PhoneFareDto 保证手机号码的唯一性
 * </p>
 *
 * @author xueyi
 * @since 2020/5/16 16:11
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PhoneFareDto implements Serializable {
    /**
     *  手机号码
     */
    private String phone;
}
